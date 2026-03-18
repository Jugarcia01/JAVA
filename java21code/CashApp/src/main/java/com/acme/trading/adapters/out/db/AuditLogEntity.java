package com.acme.trading.adapters.out.db;

import io.r2dbc.postgresql.codec.Json;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Entidad de persistencia (adapter).
 * Se mantiene separada del dominio para no acoplarlo a Spring Data.
 */
@Table("audit_logs")
public record AuditLogEntity(
  @Id Long id,
  @Column("occurred_at") Instant occurredAt,
  String level,
  String category,
  String message,
  Json payload,
  @Column("correlation_id") String correlationId
) {}