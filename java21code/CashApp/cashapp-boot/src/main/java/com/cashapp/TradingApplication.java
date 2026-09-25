package com.cashapp;

import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TradingApplication {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(TradingApplication.class);
    app.setDefaultProperties(Map.of("spring.profiles.default", "local"));
    app.run(args);
  }
}