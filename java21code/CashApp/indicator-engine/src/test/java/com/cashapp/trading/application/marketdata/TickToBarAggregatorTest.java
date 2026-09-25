package com.cashapp.trading.application.marketdata;

import com.cashapp.trading.domain.model.MarketTick;
import com.cashapp.trading.domain.model.TradeRequest;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class TickToBarAggregatorTest {

  @Test
  void aggregatesTicksIntoHourlyBarsAndClosesOnNextWindow() {
    TickToBarAggregator agg = new TickToBarAggregator();

    Flux<MarketTick> ticks = Flux.just(
      new MarketTick("NVDA", Instant.parse("2026-04-12T10:00:05Z"), 100.0, 10.0),
      new MarketTick("NVDA", Instant.parse("2026-04-12T10:10:00Z"), 101.0, 5.0),
      new MarketTick("NVDA", Instant.parse("2026-04-12T10:59:59Z"), 99.0, 2.0),
      new MarketTick("NVDA", Instant.parse("2026-04-12T11:00:00Z"), 105.0, 1.0)
    );

    StepVerifier.create(agg.aggregate(ticks, TradeRequest.Timeframe.H1))
      .assertNext(bar -> {
        org.junit.jupiter.api.Assertions.assertEquals("NVDA", bar.symbol());
        org.junit.jupiter.api.Assertions.assertEquals(TradeRequest.Timeframe.H1, bar.timeframe());
        org.junit.jupiter.api.Assertions.assertEquals(Instant.parse("2026-04-12T10:00:00Z"), bar.startTime());
        org.junit.jupiter.api.Assertions.assertEquals(Instant.parse("2026-04-12T11:00:00Z"), bar.endTime());
        org.junit.jupiter.api.Assertions.assertEquals(100.0, bar.open());
        org.junit.jupiter.api.Assertions.assertEquals(101.0, bar.high());
        org.junit.jupiter.api.Assertions.assertEquals(99.0, bar.low());
        org.junit.jupiter.api.Assertions.assertEquals(99.0, bar.close());
        org.junit.jupiter.api.Assertions.assertEquals(17.0, bar.volume());
      })
      .verifyComplete();
  }
}
