package com.acme.trading.infrastructure.config;

import com.acme.trading.application.port.in.EvaluateSignalUseCase;
import com.acme.trading.application.port.out.AuditLogRepositoryPort;
import com.acme.trading.application.port.out.MarketDataPort;
import com.acme.trading.application.usecase.EvaluateSignalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración explícita para mantener el núcleo limpio y evitar autowiring implícito excesivo.
 */
@Configuration
public class BeansConfig {

  @Bean
  EvaluateSignalUseCase evaluateSignalUseCase(MarketDataPort marketData, AuditLogRepositoryPort audit) {
    return new EvaluateSignalService(marketData, audit);
  }

  @Bean
  ObjectMapper objectMapper() {
    return new ObjectMapper().findAndRegisterModules();
  }
}
