package com.acme.trading.application.usecase;

import com.acme.trading.application.port.in.EvaluateSignalUseCase;
import com.acme.trading.application.port.out.AuditLogRepositoryPort;
import com.acme.trading.application.port.out.MarketDataPort;
import com.acme.trading.domain.model.AuditLog;
import com.acme.trading.domain.model.TradeDecision;
import com.acme.trading.domain.model.TradeRequest;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Implementación del caso de uso con lógica de orquestación:
 * - consulta datos
 * - aplica reglas
 * - registra auditoría
 * Importante: la "estrategia" aquí es simple y extensible.
 * Puedes sustituir por reglas más complejas (order flow, footprint, etc.).
 */
public final class EvaluateSignalService implements EvaluateSignalUseCase {

  private final MarketDataPort marketData;
  private final AuditLogRepositoryPort audit;

  public EvaluateSignalService(MarketDataPort marketData, AuditLogRepositoryPort audit) {
    this.marketData = marketData;
    this.audit = audit;
  }

  @Override
  public Mono<TradeDecision> evaluate(TradeRequest request, String correlationId) {
    return marketData.getSnapshot(request.symbol())
      .flatMap(snapshot -> {
        // Reglas base: tendencia diaria + confirmación 1H
        boolean trendOk = snapshot.lastPrice() > snapshot.ema20Daily()
          && snapshot.ema20Daily() > snapshot.ema50Daily()
          && snapshot.rsiDaily() >= 45 && snapshot.rsiDaily() <= 65;

        boolean momentumOk = snapshot.rsi1h() > 50
          && snapshot.volume1h() > snapshot.avgVolume1h20();

        TradeDecision decision = new TradeDecision(
          snapshot.symbol(),
          request.asOf(),
          (trendOk && momentumOk) ? TradeDecision.Action.BUY : TradeDecision.Action.HOLD,
          (trendOk && momentumOk) ? 0.62 : 0.45,
          2.0,   // stop sugerido % (ejemplo)
          4.0    // take-profit sugerido % (ejemplo)
        );

        AuditLog log = AuditLog.info(
          "SIGNAL",
          "Signal evaluated",
          Map.of(
            "symbol", request.symbol(),
            "timeframe", request.timeframe().name(),
            "trendOk", trendOk,
            "momentumOk", momentumOk,
            "action", decision.action().name(),
            "confidence", decision.confidence()
          ),
          correlationId
        );

        return audit.save(log).thenReturn(decision);
      });
  }
}