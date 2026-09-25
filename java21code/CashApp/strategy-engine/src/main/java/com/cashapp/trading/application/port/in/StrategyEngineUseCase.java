package com.cashapp.trading.application.port.in;

import com.cashapp.trading.domain.model.MarketBar;
import com.cashapp.trading.domain.model.MarketTick;
import com.cashapp.trading.domain.model.SignalEvent;
import reactor.core.publisher.Flux;

public interface StrategyEngineUseCase {

  Flux<SignalEvent> onTick(MarketTick tick, String correlationId);

  Flux<SignalEvent> onBar(MarketBar bar, String correlationId);
}
