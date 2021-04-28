package com.codesseur.mixin;

import static com.codesseur.mixin.functions.Functions.func;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Optionals {

  @SafeVarargs
  static <T> Stream<T> stream(Supplier<Optional<T>>... optionals) {
    return Stream.of(optionals).map(Supplier::get).flatMap(Optionals::stream);
  }

  static <T> Stream<T> stream(Optional<T> optional) {
    return optional.map(Stream::of).orElseGet(Stream::empty);
  }

  static <T, E> Tuple2<T, E> mandatoryAnd(Optional<T> o1, Optional<E> o2) {
    return and(o1, o2)
        .orElseThrow(() -> new IllegalArgumentException("cannot be empty : " + o1 + ", " + o2));
  }

  static <T, E> Optional<Tuple2<T, E>> and(Optional<T> o1, Optional<E> o2) {
    return join(o1, o2).combine(Tuple::of);
  }

  static <T, E> OptionalCombiner<T, E> join(Optional<T> o1, Optional<E> o2) {
    return new OptionalCombiner<>(o1, o2);
  }

  static <T> OptionalUniCombiner<T> uniJoin(Optional<T> o1, Optional<T> o2) {
    return new OptionalUniCombiner<>(o1, o2);
  }

  @SafeVarargs
  static <T> Optional<T> or(T... optionals) {
    return or(Arrays.stream(optionals).map(o -> () -> Optional.ofNullable(o)));
  }

  @SafeVarargs
  static <T> Optional<T> or(Optional<T>... optionals) {
    return or(Arrays.stream(optionals).map(o -> () -> o));
  }

  @SafeVarargs
  static <T> Optional<T> or(Supplier<Optional<T>>... optionals) {
    return or(Stream.of(optionals));
  }

  static <T> Optional<T> or(List<Supplier<Optional<T>>> optionals) {
    return or(optionals.stream());
  }

  static <T> Optional<T> or(Stream<Supplier<Optional<T>>> optionals) {
    return optionals.map(Supplier::get).flatMap(Optionals::stream).findFirst();
  }

  class OptionalUniCombiner<T> extends  OptionalCombiner<T,T>{

    private OptionalUniCombiner(Optional<T> o1, Optional<T> o2) {
      super(o1, o2);
    }

    public <R> R combine(BiFunction<? super T, ? super T, ? extends R> both, Function<? super T,? extends R> leftOrRight) {
      return combine(leftOrRight, both, leftOrRight);
    }
  }

  class OptionalCombiner<T, E> {

    private final Optional<T> o1;
    private final Optional<E> o2;

    private OptionalCombiner(Optional<T> o1, Optional<E> o2) {
      this.o1 = o1;
      this.o2 = o2;
    }

    public <R> R combine(Function<? super T, ? extends R> left, BiFunction<? super T, ? super E, ? extends R> both, Function<? super E, ? extends R> right) {
      return or(() -> o1.flatMap(f -> o2.map(s -> both.apply(f, s))), () -> o1.map(left),
          () -> o2.map(right)).orElse(null);
    }

    public <R> R combine(BiFunction<? super T, ? super E, ? extends R> both, Supplier<? extends R> leftOrRight) {
      return combine(func(leftOrRight), both, func(leftOrRight));
    }

    public <R> Optional<R> combine(BiFunction<? super T, ? super E, ? extends R> both) {
      return combine(both.andThen(Optional::ofNullable), Optional::empty);
    }

    public <R> Optional<R> combine(Function<? super T, ? extends R> left, Function<? super E, ? extends R> right) {
      return combine(left.andThen(Optional::ofNullable), (i1, i2) -> Optional.empty(),
          right.andThen(Optional::ofNullable));
    }

    public <R> Optional<R> combineLeft(Function<? super T, ? extends R> left) {
      return combine(left, i -> null);
    }

    public <R> Optional<R> combineRight(Function<? super E, ? extends R> right) {
      return combine(i -> null, right);
    }

  }

}
