package com.cashapp.trading.application.strategy;

import com.cashapp.trading.application.port.out.MarketDataPort;
import com.cashapp.trading.domain.model.TradeRequest;
import java.time.Clock;
import java.util.Map;

public record StrategyContext(
  Clock clock,
  MarketDataPort marketData,
  Map<String, Object> parameters,
  TradeRequest.Timeframe timeframe,
  String correlationId
) {}
