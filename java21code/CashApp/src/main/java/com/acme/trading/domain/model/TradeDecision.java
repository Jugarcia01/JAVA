package com.acme.trading.domain.model;

import java.time.Instant;

/**
 * Resultado de un caso de uso: la decisión (BUY/SELL/HOLD) y parámetros sugeridos.
 */
public record TradeDecision(
  String symbol,
  Instant decidedAt,
  Action action,
  double confidence,
  double suggestedStopPct,
  double suggestedTakeProfitPct
) {
  public enum Action { BUY, SELL, HOLD }
}
