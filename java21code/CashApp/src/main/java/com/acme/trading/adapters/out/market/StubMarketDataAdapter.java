package com.acme.trading.adapters.out.market;

import com.acme.trading.application.port.out.MarketDataPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Random;

/**
 * Adaptador provisional. En producción se reemplaza por:
 * - proveedor market data
 * - IBKR market data
 * - almacen histórico + indicadores
 * Mantenerlo aquí evita contaminar el dominio con detalles.
 */
@Component
public final class StubMarketDataAdapter implements MarketDataPort {

  private final Random rnd = new Random();

  @Override
  public Mono<MarketSnapshot> getSnapshot(String symbol) {
    // Simulación básica
    double last = 100 + rnd.nextDouble() * 50;
    double ema20 = last - 1.2;
    double ema50 = last - 2.5;

    return Mono.just(new MarketSnapshot(
      symbol,
      last,
      ema20,
      ema50,
      52 + rnd.nextDouble() * 10,
      51 + rnd.nextDouble() * 10,
      120000 + rnd.nextDouble() * 50000,
      110000
    ));
  }
}