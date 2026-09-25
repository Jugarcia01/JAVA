package com.cashapp.trading.adapters.in.web;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.cashapp.trading.application.marketdata.TickToBarAggregator;
import com.cashapp.trading.application.port.in.StrategyEngineUseCase;
import com.cashapp.trading.domain.model.MarketBar;
import com.cashapp.trading.domain.model.MarketTick;
import com.cashapp.trading.domain.model.TradeRequest;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public final class StrategyHandler {

  private final StrategyEngineUseCase engine;
  private final TickToBarAggregator aggregator;

  public StrategyHandler(StrategyEngineUseCase engine, TickToBarAggregator aggregator) {
    this.engine = engine;
    this.aggregator = aggregator;
  }

  public Mono<ServerResponse> ingestTick(ServerRequest req) {
    String symbol = req.queryParam("symbol").orElse("");
    double price = Double.parseDouble(req.queryParam("price").orElse("0"));
    double size = Double.parseDouble(req.queryParam("size").orElse("0"));
    boolean aggregateBars = Boolean.parseBoolean(req.queryParam("aggregateBars").orElse("false"));
    String timeframeParam = req.queryParam("timeframe").orElse("H1");

    String correlationId = req.headers().firstHeader("X-Correlation-Id");
    if (correlationId == null || correlationId.isBlank()) {
      correlationId = UUID.randomUUID().toString();
    }
    final String corr = correlationId;

    MarketTick tick = new MarketTick(symbol, Instant.now(), price, size);

    Flux<Object> out = engine.onTick(tick, corr).cast(Object.class);
    if (aggregateBars) {
      TradeRequest.Timeframe timeframe = TradeRequest.Timeframe.valueOf(timeframeParam);
      Flux<MarketBar> bars = aggregator.aggregate(Flux.just(tick), timeframe);
      out = out.mergeWith(bars.flatMap(bar -> engine.onBar(bar, corr)).cast(Object.class));
    }

    return ok().contentType(MediaType.APPLICATION_JSON)
      .body(out.collectList(), Object.class);
  }

  public Mono<ServerResponse> ingestTicks(ServerRequest req) {
    boolean aggregateBars = Boolean.parseBoolean(req.queryParam("aggregateBars").orElse("true"));
    String timeframeParam = req.queryParam("timeframe").orElse("H1");

    String correlationId = req.headers().firstHeader("X-Correlation-Id");
    if (correlationId == null || correlationId.isBlank()) {
      correlationId = UUID.randomUUID().toString();
    }
    final String corr = correlationId;

    TradeRequest.Timeframe timeframe = TradeRequest.Timeframe.valueOf(timeframeParam);

    MediaType ct = req.headers().contentType().orElse(MediaType.APPLICATION_JSON);

    Flux<MarketTick> ticks = MediaType.APPLICATION_NDJSON.isCompatibleWith(ct)
      ? req.bodyToFlux(MarketTick.class)
      : req.bodyToMono(MarketTick.class).flux();

    Flux<MarketTick> sharedTicks = ticks.share();

    Flux<Object> signalsFromTicks = sharedTicks
      .flatMap(tick -> engine.onTick(tick, corr))
      .cast(Object.class);

    Flux<Object> signalsFromBars = aggregateBars
      ? aggregator.aggregate(sharedTicks, timeframe)
        .flatMap(bar -> engine.onBar(bar, corr))
        .cast(Object.class)
      : Flux.empty();

    Flux<Object> out = signalsFromTicks.mergeWith(signalsFromBars);

    return ok().contentType(MediaType.APPLICATION_NDJSON)
      .body(out, Object.class);
  }
}
