package com.codesseur.functions;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class gather some of the most common function conversions
 */
public interface Functions {

  /**
   * lift  a function to take optional
   *
   * @param function
   * @param <T>
   * @param <R>
   * @return Lifted functuin
   */
  static <T, R> Function<Optional<T>, Optional<R>> lift(Function<? super T, ? extends R> function) {
    return o -> o.map(function);
  }

  static <T> Supplier<T> supplier(T value) {
    return () -> value;
  }

  static <T> Function<T, T> func(Consumer<? super T> consumer) {
    return o -> {
      consumer.accept(o);
      return o;
    };
  }

  static <T, E> BiFunction<T, E, T> func(BiConsumer<? super T, ? super E> consumer) {
    return (o1, o2) -> {
      consumer.accept(o1, o2);
      return o1;
    };
  }

  static <T, E> BiFunction<T, E, E> func2(BiConsumer<? super T, ? super E> consumer) {
    return (o1, o2) -> {
      consumer.accept(o1, o2);
      return o2;
    };
  }

  static <T, R> Function<T, R> func(Supplier<? extends R> supplier) {
    return o -> supplier.get();
  }

}
