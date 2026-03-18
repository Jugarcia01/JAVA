package com.acme.trading.adapters.in.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * Router WebFlux: define endpoints funcionales.
 */
@Configuration
public class SignalRouter {

  @Bean
  RouterFunction<?> routes(SignalHandler handler) {
    return route(GET("/signals/evaluate"), handler::evaluate);
  }
}