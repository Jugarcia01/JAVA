package com.cashapp.trading.application.marketdata;

import com.cashapp.trading.domain.model.MarketBar;
import com.cashapp.trading.domain.model.MarketTick;
import com.cashapp.trading.domain.model.TradeRequest;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import reactor.core.publisher.Flux;

public final class TickToBarAggregator {

  private final Map<Key, MutableBar> state = new ConcurrentHashMap<>();

  public Flux<MarketBar> aggregate(Flux<MarketTick> ticks, TradeRequest.Timeframe timeframe) {
    Objects.requireNonNull(ticks, "ticks");
    Objects.requireNonNull(timeframe, "timeframe");

    return ticks.handle((tick, sink) -> {
      if (tick == null || tick.symbol() == null || tick.symbol().isBlank()) {
        return;
      }

      Duration bucket = bucketDuration(timeframe);
      Instant windowStart = floorToBucket(tick.eventTime(), bucket);
      Instant windowEnd = windowStart.plus(bucket);

      Key key = new Key(tick.symbol(), timeframe);

      state.compute(key, (k, current) -> {
        if (current == null) {
          return MutableBar.start(tick.symbol(), timeframe, windowStart, windowEnd, tick.price(), tick.size());
        }

        if (tick.eventTime().isBefore(current.startTime)) {
          return current;
        }

        if (tick.eventTime().isBefore(current.endTime)) {
          current.update(tick.price(), tick.size());
          return current;
        }

        sink.next(current.toImmutable());
        return MutableBar.start(tick.symbol(), timeframe, windowStart, windowEnd, tick.price(), tick.size());
      });
    });
  }

  public Flux<MarketBar> flush() {
    return Flux.fromIterable(state.values())
      .map(MutableBar::toImmutable)
      .doFinally(signalType -> state.clear());
  }

  private static Duration bucketDuration(TradeRequest.Timeframe timeframe) {
    return switch (timeframe) {
      case H1 -> Duration.ofHours(1);
      case D1 -> Duration.ofDays(1);
    };
  }

  private static Instant floorToBucket(Instant time, Duration bucket) {
    if (time == null) {
      return Instant.EPOCH;
    }

    long seconds = time.getEpochSecond();
    long bucketSeconds = bucket.getSeconds();
    long floored = (seconds / bucketSeconds) * bucketSeconds;

    ZonedDateTime zdt = Instant.ofEpochSecond(floored).atZone(ZoneOffset.UTC);
    if (bucket.equals(Duration.ofDays(1))) {
      zdt = zdt.toLocalDate().atStartOfDay(ZoneOffset.UTC);
    }
    return zdt.toInstant();
  }

  private record Key(String symbol, TradeRequest.Timeframe timeframe) {}

  private static final class MutableBar {
    private final String symbol;
    private final TradeRequest.Timeframe timeframe;
    private final Instant startTime;
    private final Instant endTime;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;

    private MutableBar(String symbol,
                       TradeRequest.Timeframe timeframe,
                       Instant startTime,
                       Instant endTime,
                       double open,
                       double high,
                       double low,
                       double close,
                       double volume)
    {
      this.symbol = symbol;
      this.timeframe = timeframe;
      this.startTime = startTime;
      this.endTime = endTime;
      this.open = open;
      this.high = high;
      this.low = low;
      this.close = close;
      this.volume = volume;
    }

    static MutableBar start(String symbol,
                            TradeRequest.Timeframe timeframe,
                            Instant startTime,
                            Instant endTime,
                            double firstPrice,
                            double firstSize)
    {
      return new MutableBar(symbol, timeframe, startTime, endTime, firstPrice, firstPrice, firstPrice, firstPrice, firstSize);
    }

    void update(double price, double size) {
      close = price;
      high = Math.max(high, price);
      low = Math.min(low, price);
      volume += size;
    }

    MarketBar toImmutable() {
      return new MarketBar(
        symbol,
        timeframe,
        startTime,
        endTime,
        open,
        high,
        low,
        close,
        volume
      );
    }
  }
}
