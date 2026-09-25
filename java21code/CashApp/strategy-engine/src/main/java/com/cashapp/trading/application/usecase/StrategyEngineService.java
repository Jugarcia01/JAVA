package com.cashapp.trading.application.usecase;

import com.cashapp.trading.application.port.in.StrategyEngineUseCase;
import com.cashapp.trading.application.port.out.AuditLogRepositoryPort;
import com.cashapp.trading.application.port.out.MarketDataPort;
import com.cashapp.trading.application.port.out.SignalPublisherPort;
import com.cashapp.trading.application.strategy.Strategy;
import com.cashapp.trading.application.strategy.StrategyContext;
import com.cashapp.trading.application.strategy.StrategyRegistry;
import com.cashapp.trading.domain.model.AuditLog;
import com.cashapp.trading.domain.model.MarketBar;
import com.cashapp.trading.domain.model.MarketTick;
import com.cashapp.trading.domain.model.SignalEvent;
import com.cashapp.trading.domain.model.TradeRequest;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Flux;

public final class StrategyEngineService implements StrategyEngineUseCase {

  private final StrategyRegistry registry;
  private final Clock clock;
  private final MarketDataPort marketData;
  private final SignalPublisherPort signalPublisher;
  private final AuditLogRepositoryPort audit;

  public StrategyEngineService(StrategyRegistry registry,
                              Clock clock,
                              MarketDataPort marketData,
                              SignalPublisherPort signalPublisher,
                              AuditLogRepositoryPort audit)
  {
    this.registry = registry;
    this.clock = clock;
    this.marketData = marketData;
    this.signalPublisher = signalPublisher;
    this.audit = audit;
  }

  @Override
  public Flux<SignalEvent> onTick(MarketTick tick, String correlationId) {
    TradeRequest.Timeframe timeframe = TradeRequest.Timeframe.H1;
    StrategyContext ctx = new StrategyContext(clock, marketData, Map.of(), timeframe, correlationId);

    List<Strategy> strategies = registry.findApplicable(tick.symbol(), timeframe);

    return Flux.fromIterable(strategies)
      .flatMap(strategy -> strategy.onTick(tick, ctx))
      .flatMap(event -> auditAndPublish(event).thenReturn(event));
  }

  @Override
  public Flux<SignalEvent> onBar(MarketBar bar, String correlationId) {
    StrategyContext ctx = new StrategyContext(clock, marketData, Map.of(), bar.timeframe(), correlationId);

    List<Strategy> strategies = registry.findApplicable(bar.symbol(), bar.timeframe());

    return Flux.fromIterable(strategies)
      .flatMap(strategy -> strategy.onBar(bar, ctx))
      .flatMap(event -> auditAndPublish(event).thenReturn(event));
  }

  private reactor.core.publisher.Mono<Void> auditAndPublish(SignalEvent event) {
    AuditLog log = AuditLog.info(
      "SIGNAL",
      "Signal emitted",
      Map.of(
        "strategyId", event.strategyId(),
        "symbol", event.symbol(),
        "timeframe", event.timeframe().name(),
        "action", event.action().name(),
        "confidence", event.confidence()
      ),
      event.correlationId()
    );

    return audit.save(log)
      .then(signalPublisher.publish(event));
  }
}
