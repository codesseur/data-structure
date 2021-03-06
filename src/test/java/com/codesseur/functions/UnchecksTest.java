package com.codesseur.functions;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class UnchecksTest {

  @Test
  void uncheckConsumer() {
    AtomicBoolean b = new AtomicBoolean();
    Consumer<Boolean> f = Unchecks.Consume.uncheck(b::set);

    f.accept(true);

    Assertions.assertThat(b).isTrue();
  }

  @Test
  void uncheckFunction() {
    AtomicBoolean b = new AtomicBoolean();
    Function<Boolean, Boolean> f = Unchecks.Func.uncheck(newValue -> {
      b.set(newValue);
      return newValue;
    });

    Boolean result = f.apply(true);

    Assertions.assertThat(result).isTrue();
    Assertions.assertThat(b).isTrue();
  }

  @Test
  void uncheckBiFunction() {
    BiFunction<Boolean, Boolean, String> f = Unchecks.Func.uncheck((v1, v2) -> "" + v1 + v2);

    String result = f.apply(true, true);

    Assertions.assertThat(result).isEqualTo("truetrue");
  }

  @Test
  void uncheckPredicate() {
    Predicate<String> f = Unchecks.Test.uncheck(String::isEmpty);

    boolean result = f.test("value");

    Assertions.assertThat(result).isFalse();
  }

  @Test
  void uncheckSupplier() {
    Supplier<String> f = Unchecks.Supply.uncheck(() -> "value");

    String result = f.get();

    Assertions.assertThat(result).isEqualTo("value");
  }
}