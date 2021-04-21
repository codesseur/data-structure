package com.codesseur.mixin.iterate.container;

import static java.util.function.Function.identity;

import com.codesseur.mixin.Optionals;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import com.codesseur.mixin.Void;
import com.codesseur.mixin.iterate.Streamed;

public interface Sequence<T> extends CollectionContainer<T, List<T>> {

  static <T> Sequence<T> empty() {
    return of(Stream.empty());
  }

  @SafeVarargs
  static <T> Sequence<T> of(Sequence<T>... value) {
    return of(Stream.of(value).flatMap(Container::stream));
  }

  @SafeVarargs
  static <T> Sequence<T> of(T... value) {
    return of(Stream.of(value));
  }

  @SafeVarargs
  static <T> Sequence<T> of(Optional<T>... value) {
    return of(Stream.of(value).flatMap(Optionals::stream));
  }

  @SafeVarargs
  static <T> Sequence<Optional<T>> ofNullables(T... value) {
    return of(Stream.of(value).map(Optional::ofNullable));
  }

  static <T> Sequence<T> repeat(IntFunction<T> factory, int times) {
    return of(IntStream.range(0, times).mapToObj(factory));
  }

  static Sequence<Void> repeat(int times) {
    return repeat(i -> Void.INSTANCE, times);
  }

  static <T> Sequence<T> of(List<? extends T> value) {
    return new SimpleSequence<T>((List<T>) value);
  }

  static <T> Sequence<Optional<T>> ofNullables(List<T> value) {
    return ofNullables(value.stream());
  }

  static <T> Sequence<T> of(Iterator<T> value) {
    return of(Streamed.from(value));
  }

  static <T> Sequence<Optional<T>> ofNullables(Iterator<T> value) {
    return ofNullables(Streamed.from(value));
  }

  @SafeVarargs
  static <T> Sequence<Optional<T>> ofNullables(Stream<T>... value) {
    return of(Optional::ofNullable, value);
  }

  @SafeVarargs
  static <T> Sequence<T> of(Stream<? extends T>... value) {
    return of(identity(), value);
  }

  @SafeVarargs
  static <T, E> Sequence<E> of(Function<T, E> mapper, Stream<? extends T>... value) {
    return Streamed.from(value).map(mapper).toSequence();
  }

  default Optional<T> get(int index) {
    return Optional.ofNullable(value()).filter(v -> v.size() > index).map(v -> v.get(index));
  }

  default Sequence<T> trim(Predicate<T> empty) {
    return trimLeft(empty).trimRight(empty);
  }

  default Sequence<T> trimLeft(Predicate<T> empty) {
    List<T> value = new ArrayList<>(value());
    ListIterator<T> it = value.listIterator();
    while (it.hasNext() && empty.test(it.next())) {
      it.remove();
    }
    return of(value);
  }

  default Sequence<T> trimRight(Predicate<T> empty) {
    List<T> value = new ArrayList<>(value());
    ListIterator<T> it = value.listIterator(value.size());
    while (it.hasPrevious() && empty.test(it.previous())) {
      it.remove();
    }
    return of(value);
  }

  default Optional<Sequence<T>> toOptional() {
    return isEmpty() ? Optional.empty() : Optional.of(this);
  }
}
