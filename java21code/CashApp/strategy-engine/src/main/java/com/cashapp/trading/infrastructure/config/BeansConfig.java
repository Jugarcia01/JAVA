package com.cashapp.trading.infrastructure.config;

import com.cashapp.trading.application.port.in.EvaluateSignalUseCase;
import com.cashapp.trading.application.port.in.StrategyEngineUseCase;
import com.cashapp.trading.application.port.out.AuditLogRepositoryPort;
import com.cashapp.trading.application.port.out.MarketDataPort;
import com.cashapp.trading.application.port.out.SignalPublisherPort;
import com.cashapp.trading.application.marketdata.TickToBarAggregator;
import com.cashapp.trading.application.strategy.Strategy;
import com.cashapp.trading.application.strategy.StrategyRegistry;
import com.cashapp.trading.application.usecase.EvaluateSignalService;
import com.cashapp.trading.application.usecase.StrategyEngineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.util.List;
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
  Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  StrategyRegistry strategyRegistry(List<Strategy> strategies) {
    return new StrategyRegistry(strategies);
  }

  @Bean
  StrategyEngineUseCase strategyEngineUseCase(StrategyRegistry registry,
                                             Clock clock,
                                             MarketDataPort marketData,
                                             SignalPublisherPort signalPublisher,
                                             AuditLogRepositoryPort audit)
  {
    return new StrategyEngineService(registry, clock, marketData, signalPublisher, audit);
  }

  @Bean
  TickToBarAggregator tickToBarAggregator() {
    return new TickToBarAggregator();
  }

  @Bean
  ObjectMapper objectMapper() {
    return new ObjectMapper().findAndRegisterModules();
  }
}
