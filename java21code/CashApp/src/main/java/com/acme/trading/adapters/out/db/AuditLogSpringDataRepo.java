package com.acme.trading.adapters.out.db;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AuditLogSpringDataRepo extends ReactiveCrudRepository<AuditLogEntity, Long> {}
