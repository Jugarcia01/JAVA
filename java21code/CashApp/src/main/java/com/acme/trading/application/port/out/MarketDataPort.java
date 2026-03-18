package com.acme.trading.application.port.out;

import reactor.core.publisher.Mono;

/**
 * Puerto de salida: obtención de datos de mercado (precios/volumen/indicadores).
 * En producción, podría ir a un proveedor o a IBKR/market-data provider.
 */
public interface MarketDataPort {

  Mono<MarketSnapshot> getSnapshot(String symbol);

  record MarketSnapshot(
    String symbol,
    double lastPrice,
    double ema20Daily,
    double ema50Daily,
    double rsiDaily,
    double rsi1h,
    double volume1h,
    double avgVolume1h20
  ) {}
}
