package com.codesseur.functions;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class FunctionsTest {

  @Test
  void lift() {
    Function<Optional<String>, Optional<String>> f = Functions.lift(v -> v + v);

    Optional<String> actual = f.apply(Optional.of("v"));

    Assertions.assertThat(actual).hasValue("vv");
  }

  @Test
  void supplier() {
    Supplier<String> f = Functions.supplier("v");

    Assertions.assertThat(f.get()).isEqualTo("v");
  }

  @Test
  void functionizeConsumer() {
    AtomicBoolean b = new AtomicBoolean();
    Function<Boolean, Boolean> f = Functions.func(b::set);

    final Boolean result = f.apply(true);

    Assertions.assertThat(result).isTrue();
    Assertions.assertThat(b).isTrue();
  }

  @Test
  void functionizeBiConsumer() {
    AtomicBoolean b1 = new AtomicBoolean();
    AtomicBoolean b2 = new AtomicBoolean();
    BiFunction<Boolean, Boolean, Boolean> f = Functions.func((v1, v2) -> {
      b1.set(v1);
      b2.set(v2);
    });

    Boolean result = f.apply(true, false);

    Assertions.assertThat(result).isTrue();
    Assertions.assertThat(b1).isTrue();
    Assertions.assertThat(b2).isFalse();
  }

  @Test
  void functionizeSecondBiConsumer() {
    AtomicBoolean b1 = new AtomicBoolean();
    AtomicBoolean b2 = new AtomicBoolean();
    BiFunction<Boolean, Boolean, Boolean> f = Functions.func2((v1, v2) -> {
      b1.set(v1);
      b2.set(v2);
    });

    Boolean result = f.apply(true, false);

    Assertions.assertThat(result).isFalse();
    Assertions.assertThat(b1).isTrue();
    Assertions.assertThat(b2).isFalse();
  }

  @Test
  void functionizeSupplier() {
    Function<Boolean, Boolean> f = Functions.func(() -> true);

    Assertions.assertThat(f.apply(false)).isTrue();
  }

}