package com.cashapp.trading.domain.model;

import java.time.Instant;

public record MarketBar(
  String symbol,
  TradeRequest.Timeframe timeframe,
  Instant startTime,
  Instant endTime,
  double open,
  double high,
  double low,
  double close,
  double volume
) {}
