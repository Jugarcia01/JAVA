package com.cashapp.trading.application.strategy.strategies;

import com.cashapp.trading.application.strategy.Strategy;
import com.cashapp.trading.application.strategy.StrategyContext;
import com.cashapp.trading.application.port.out.MarketDataPort.MarketSnapshot;
import com.cashapp.trading.domain.model.MarketBar;
import com.cashapp.trading.domain.model.MarketTick;
import com.cashapp.trading.domain.model.SignalEvent;
import com.cashapp.trading.domain.model.TradeDecision;
import com.cashapp.trading.domain.model.TradeRequest;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public final class TrendMomentumStrategy implements Strategy {

  @Override
  public String id() {
    return "trend-momentum-v1";
  }

  @Override
  public boolean supports(String symbol, TradeRequest.Timeframe timeframe) {
    return symbol != null && !symbol.isBlank();
  }

  @Override
  public Flux<SignalEvent> onTick(MarketTick tick, StrategyContext context) {
    return context.marketData().getSnapshot(tick.symbol())
      .flux()
      .map(snapshot -> toSignal(snapshot, context.timeframe(), context.correlationId(), context.clock().instant()));
  }

  @Override
  public Flux<SignalEvent> onBar(MarketBar bar, StrategyContext context) {
    return context.marketData().getSnapshot(bar.symbol())
      .flux()
      .map(snapshot -> toSignal(snapshot, bar.timeframe(), context.correlationId(), context.clock().instant()));
  }

  private SignalEvent toSignal(MarketSnapshot snapshot,
                              TradeRequest.Timeframe timeframe,
                              String correlationId,
                              Instant decidedAt)
  {
    boolean trendOk = snapshot.lastPrice() > snapshot.ema20Daily()
      && snapshot.ema20Daily() > snapshot.ema50Daily()
      && snapshot.rsiDaily() >= 45 && snapshot.rsiDaily() <= 65;

    boolean momentumOk = snapshot.rsi1h() > 50
      && snapshot.volume1h() > snapshot.avgVolume1h20();

    TradeDecision.Action action = (trendOk && momentumOk) ? TradeDecision.Action.BUY : TradeDecision.Action.HOLD;
    double confidence = (trendOk && momentumOk) ? 0.62 : 0.45;

    return new SignalEvent(
      id(),
      snapshot.symbol(),
      timeframe,
      decidedAt,
      action,
      confidence,
      Map.of(
        "trendOk", trendOk,
        "momentumOk", momentumOk,
        "lastPrice", snapshot.lastPrice(),
        "ema20Daily", snapshot.ema20Daily(),
        "ema50Daily", snapshot.ema50Daily(),
        "rsiDaily", snapshot.rsiDaily(),
        "rsi1h", snapshot.rsi1h(),
        "volume1h", snapshot.volume1h(),
        "avgVolume1h20", snapshot.avgVolume1h20()
      ),
      correlationId
    );
  }
}
