package com.cashapp.trading.domain.model;

import java.time.Instant;
import java.util.Map;

public record SignalEvent(
  String strategyId,
  String symbol,
  TradeRequest.Timeframe timeframe,
  Instant decidedAt,
  TradeDecision.Action action,
  double confidence,
  Map<String, Object> features,
  String correlationId
) {}
