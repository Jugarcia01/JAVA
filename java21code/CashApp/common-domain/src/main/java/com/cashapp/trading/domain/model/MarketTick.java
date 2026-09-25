package com.cashapp.trading.domain.model;

import java.time.Instant;

public record MarketTick(
  String symbol,
  Instant eventTime,
  double price,
  double size
) {}
