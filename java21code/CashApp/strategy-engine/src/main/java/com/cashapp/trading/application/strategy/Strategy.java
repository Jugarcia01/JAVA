package com.cashapp.trading.application.strategy;

import com.cashapp.trading.domain.model.MarketBar;
import com.cashapp.trading.domain.model.MarketTick;
import com.cashapp.trading.domain.model.SignalEvent;
import com.cashapp.trading.domain.model.TradeRequest;
import reactor.core.publisher.Flux;

public interface Strategy {

  String id();

  boolean supports(String symbol, TradeRequest.Timeframe timeframe);

  Flux<SignalEvent> onTick(MarketTick tick, StrategyContext context);

  Flux<SignalEvent> onBar(MarketBar bar, StrategyContext context);
}
