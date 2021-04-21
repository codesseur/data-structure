package com.codesseur.mixin.functions;

import io.vavr.CheckedConsumer;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedFunction1;
import io.vavr.CheckedFunction2;
import io.vavr.CheckedPredicate;
import io.vavr.control.Try;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Unchecks {

  static <T, E> Function<T, E> uncheck(CheckedFunction1<? super T, ? extends E> function) {
    return o -> Try.of(() -> function.apply(o)).get();
  }

  static <T1, T2, E> BiFunction<T1, T2, E> uncheck(CheckedFunction2<? super T1, ? super T2, ? extends E> function) {
    return (o1, o2) -> Try.of(() -> function.apply(o1, o2)).get();
  }

  static <T> Supplier<T> uncheck(CheckedFunction0<? extends T> supplier) {
    return () -> Try.of(supplier).get();
  }

  static <T> Predicate<T> uncheck(CheckedPredicate<? super T> predicate) {
    return o -> Try.of(() -> predicate.test(o)).get();
  }

  static <T> Consumer<T> uncheck(CheckedConsumer<? super T> predicate) {
    return o -> Try.run(() -> predicate.accept(o)).get();
  }
}
