package com.acme.trading.adapters.out.db;

import com.acme.trading.application.port.out.AuditLogRepositoryPort;
import com.acme.trading.domain.model.AuditLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Adaptador de salida que implementa el puerto de auditoría usando R2DBC.
 */
@Component
public final class R2dbcAuditLogRepository implements AuditLogRepositoryPort {

  private final AuditLogSpringDataRepo repo;
  private final ObjectMapper objectMapper;

  public R2dbcAuditLogRepository(AuditLogSpringDataRepo repo, ObjectMapper objectMapper) {
    this.repo = repo;
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> save(AuditLog log) {
    return Mono.fromCallable(() -> toEntity(log))
      .flatMap(repo::save)
      .then();
  }

  private AuditLogEntity toEntity(AuditLog log) {
    String payloadJson;
    try {
      payloadJson = (log.payload() == null) ? null : objectMapper.writeValueAsString(log.payload());
    } catch (JsonProcessingException e) {
      // No rompemos el flujo por auditoría; degradamos payload.
      payloadJson = "{\"error\":\"payload-serialization-failed\"}";
    }

    Json payload = (payloadJson == null) ? null : Json.of(payloadJson);

    return new AuditLogEntity(
      null,
      log.occurredAt(),
      log.level(),
      log.category(),
      log.message(),
      payload,
      log.correlationId()
    );
  }
}
