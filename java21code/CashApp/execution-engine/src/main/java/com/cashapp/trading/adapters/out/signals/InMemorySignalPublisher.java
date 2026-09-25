package com.cashapp.trading.adapters.out.signals;

import com.cashapp.trading.application.port.out.SignalPublisherPort;
import com.cashapp.trading.domain.model.SignalEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public final class InMemorySignalPublisher implements SignalPublisherPort {

  private static final Logger log = LoggerFactory.getLogger(InMemorySignalPublisher.class);

  @Override
  public Mono<Void> publish(SignalEvent event) {
    log.info("SIGNAL strategyId={} symbol={} timeframe={} action={} confidence={} correlationId={}",
      event.strategyId(),
      event.symbol(),
      event.timeframe(),
      event.action(),
      event.confidence(),
      event.correlationId()
    );
    return Mono.empty();
  }
}
