package com.acme.trading.adapters.in.web;

import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import com.acme.trading.application.port.in.EvaluateSignalUseCase;
import com.acme.trading.domain.model.TradeRequest;
import jakarta.validation.Validator;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Handler funcional WebFlux: entrada HTTP -> caso de uso.
 * No contiene lógica de negocio, solo traducción, validación y mapeo.
 */
@Component
public final class SignalHandler {

  private final EvaluateSignalUseCase useCase;
  private final SpringValidatorAdapter validator;

  public SignalHandler(EvaluateSignalUseCase useCase, Validator validator) {
    this.useCase = useCase;
    this.validator = new SpringValidatorAdapter(validator);
  }

  public Mono<ServerResponse> evaluate(ServerRequest req) {
    String symbol = req.queryParam("symbol").orElse("");
    String timeframe = req.queryParam("timeframe").orElse("H1");

    TradeRequest request = new TradeRequest(
      symbol,
      Instant.now(),
      TradeRequest.Timeframe.valueOf(timeframe)
    );

    var errors = new BeanPropertyBindingResult(request, "tradeRequest");
    validator.validate(request, errors);
    if (errors.hasErrors()) {
      return badRequest().contentType(MediaType.APPLICATION_JSON)
        .bodyValue(errors.getAllErrors());
    }

    String correlationId = req.headers()
      .firstHeader("X-Correlation-Id");
    if (correlationId == null || correlationId.isBlank()) {
      correlationId = UUID.randomUUID().toString();
    }

    return useCase.evaluate(request, correlationId)
      .flatMap(decision -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(decision));
  }
}