package com.cashapp.trading.adapters.in.web;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

@Configuration
public class StrategyRouter {

  @Bean
  RouterFunction<?> strategyRoutes(StrategyHandler handler) {
    return route(GET("/strategy/ingest/tick"), handler::ingestTick)
      .andRoute(POST("/strategy/ingest/ticks"), handler::ingestTicks);
  }
}
