package com.codesseur.mixin.functions;

import io.vavr.CheckedConsumer;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedFunction1;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Functions {

  static <T, R> Function<Optional<T>, Optional<R>> lift(Function<? super T, ? extends R> function) {
    return o -> o.map(function);
  }

  static <T> Supplier<T> supplier(T value) {
    return () -> value;
  }

  static <T> Function<T, T> functionize(Consumer<? super T> consumer) {
    return o -> {
      consumer.accept(o);
      return o;
    };
  }

  static <T, E> BiFunction<T, E, T> functionize(BiConsumer<? super T, ? super E> consumer) {
    return (o1, o2) -> {
      consumer.accept(o1, o2);
      return o1;
    };
  }

  static <T> BinaryOperator<T> functionizeOperator(BiConsumer<? super T, ? super T> consumer) {
    return (o1, o2) -> {
      consumer.accept(o1, o2);
      return o1;
    };
  }

  static <T, E> BiFunction<T, E, E> functionizeSecond(BiConsumer<? super T, ? super E> consumer) {
    return (o1, o2) -> {
      consumer.accept(o1, o2);
      return o2;
    };
  }

  static <T, R> Function<T, R> functionize(Supplier<? extends R> supplier) {
    return o -> supplier.get();
  }

  static <T> CheckedFunction1<T, T> functionizeChecked(CheckedConsumer<? super T> consumer) {
    return o -> {
      consumer.accept(o);
      return o;
    };
  }

  static <T, TT> CheckedFunction1<T, TT> functionizeChecked(CheckedFunction0<? extends TT> supplier) {
    return o -> supplier.apply();
  }

}
