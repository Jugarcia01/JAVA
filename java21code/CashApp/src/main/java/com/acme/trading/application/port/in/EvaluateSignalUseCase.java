package com.acme.trading.application.port.in;

import com.acme.trading.domain.model.TradeDecision;
import com.acme.trading.domain.model.TradeRequest;
import reactor.core.publisher.Mono;

/**
 * Puerto de entrada (Inbound Port).
 * Define el contrato para evaluar una señal. No menciona HTTP, Camel ni DB.
 */
public interface EvaluateSignalUseCase {
  Mono<TradeDecision> evaluate(TradeRequest request, String correlationId);
}