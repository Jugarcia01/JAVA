package com.acme.trading.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * AuditLog representa un registro persistible de eventos relevantes del sistema.
 * En arquitectura hexagonal, el dominio define el "qué" (estructura de datos y significado),
 * no el "cómo" (DB, frameworks, transporte).
 */
public record AuditLog(
  UUID id,
  Instant occurredAt,
  String level,          // INFO/WARN/ERROR (string simple para evitar dependencia externa)
  String category,       // e.g. "SIGNAL", "ORDER", "INTEGRATION"
  String message,
  Map<String, Object> payload,
  String correlationId
)
{
  public static AuditLog info(String category,
                              String message,
                              Map<String, Object> payload,
                              String correlationId)
  {
    return new AuditLog(UUID.randomUUID(), Instant.now(), "INFO", category, message, payload, correlationId);
  }
}
