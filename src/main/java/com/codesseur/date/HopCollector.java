package com.codesseur.date;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class HopCollector< R> implements Collector<Object, R, R> {

  private R current;
  private final Function<R, R> reducer;
  private boolean skippedHead;

  private HopCollector(R zero, Function<R, R> reducer) {
    this.current = zero;
    this.reducer = reducer;
    this.skippedHead = false;
  }

  public static <R> HopCollector<R> of(R zero, Function<R, R> reducer) {
    return new HopCollector<>(zero, reducer);
  }

  @Override
  public Supplier<R> supplier() {
    return () -> current;
  }

  @Override
  public BiConsumer<R, Object> accumulator() {
    return (r, t) -> {
      if (skippedHead) {
        current = reducer.apply(current);
      } else {
        skippedHead = true;
      }
    };
  }

  @Override
  public BinaryOperator<R> combiner() {
    return (r1, r2) -> {
      throw new UnsupportedOperationException("collector not parallelized");
    };
  }

  @Override
  public Function<R, R> finisher() {
    return r -> current;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return Collections.emptySet();
  }
}
