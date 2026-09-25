package com.cashapp.trading.application.port.out;

import com.cashapp.trading.domain.model.SignalEvent;
import reactor.core.publisher.Mono;

public interface SignalPublisherPort {
  Mono<Void> publish(SignalEvent event);
}
