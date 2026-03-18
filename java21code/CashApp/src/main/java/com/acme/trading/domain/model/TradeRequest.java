package com.acme.trading.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Solicitud para evaluar una señal de trading.
 * En esta fase NO ejecutamos órdenes reales: evaluamos una decisión y registramos.
 */
public record TradeRequest(
  @NotBlank String symbol,        // "NVDA" | "GOOG"
  @NotNull Instant asOf,           // timestamp de evaluación
  @NotNull Timeframe timeframe     // por ejemplo 1H, 1D
) {
  public enum Timeframe { H1, D1 }
}
