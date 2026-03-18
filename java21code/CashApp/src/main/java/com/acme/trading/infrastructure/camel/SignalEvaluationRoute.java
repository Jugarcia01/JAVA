package com.acme.trading.infrastructure.camel;

import com.acme.trading.application.port.in.EvaluateSignalUseCase;
import com.acme.trading.domain.model.TradeRequest;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Ruta Camel ejemplo:
 * - from("direct:signal-evaluate") recibe un symbol/timeframe en headers
 * - invoca el caso de uso
 * - retorna la decisión como body
 * Nota: Camel no debe contener reglas de negocio, solo orquestación de integración.
 */
@Component
public final class SignalEvaluationRoute extends RouteBuilder {

  private final EvaluateSignalUseCase useCase;

  public SignalEvaluationRoute(EvaluateSignalUseCase useCase) {
    this.useCase = useCase;
  }

  @Override
  public void configure() {
    from("direct:signal-evaluate")
      .routeId("signal-evaluate-route")
      .process(exchange -> {
        String symbol = exchange.getIn().getHeader("symbol", String.class);
        String timeframe = exchange.getIn().getHeader("timeframe", String.class);
        if (timeframe == null) timeframe = "H1";

        exchange.getIn().setBody(new TradeRequest(symbol, Instant.now(), TradeRequest.Timeframe.valueOf(timeframe)));
        String corr = exchange.getIn().getHeader("correlationId", String.class);
        if (corr == null || corr.isBlank()) corr = UUID.randomUUID().toString();
        exchange.getIn().setHeader("correlationId", corr);
      })
      // integrar reactivo: convertir Mono a resultado usando camel-reactive-streams o toD con bean
      .bean(this, "evaluateReactive(${body}, ${header.correlationId})");
  }

  /**
   * Func invocable desde Camel.
   * Devuelve un Mono; Camel puede adaptarlo con reactive-streams.
   */
  public reactor.core.publisher.Mono<?> evaluateReactive(TradeRequest req, String correlationId) {
    return useCase.evaluate(req, correlationId);
  }
}
