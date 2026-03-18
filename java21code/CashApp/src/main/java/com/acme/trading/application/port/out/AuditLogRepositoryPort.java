package com.acme.trading.application.port.out;

import com.acme.trading.domain.model.AuditLog;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida: persistencia de auditoría.
 * Implementación concreta irá en adapters/out/db.
 */
public interface AuditLogRepositoryPort {
  Mono<Void> save(AuditLog log);
}
