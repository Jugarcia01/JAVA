package com.cashapp.trading.application.strategy;

import com.cashapp.trading.domain.model.TradeRequest;
import java.util.List;

public final class StrategyRegistry {

  private final List<Strategy> strategies;

  public StrategyRegistry(List<Strategy> strategies) {
    this.strategies = List.copyOf(strategies);
  }

  public List<Strategy> all() {
    return strategies;
  }

  public List<Strategy> findApplicable(String symbol, TradeRequest.Timeframe timeframe) {
    return strategies.stream()
      .filter(s -> s.supports(symbol, timeframe))
      .toList();
  }
}
