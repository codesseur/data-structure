package com.codesseur.mixin.iterate.container;

import com.codesseur.mixin.Optionals;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import com.codesseur.mixin.iterate.Streamed;
import java.util.stream.StreamSupport;

public interface Sequence<T> extends CollectionContainer<T, List<T>> {

  static <T> Sequence<T> empty() {
    return of(Stream.empty());
  }

  @SafeVarargs
  static <T> Sequence<T> noEmptyOf(Optional<T>... values) {
    return of(Stream.of(values).flatMap(Optionals::stream));
  }

  @SafeVarargs
  static <T> Sequence<T> nonNullOf(T... values) {
    return of(Stream.of(values).filter(Objects::nonNull));
  }

  @SafeVarargs
  static <T> Sequence<T> of(Sequence<T>... values) {
    return of(Stream.of(values).flatMap(Container::stream));
  }

  @SafeVarargs
  static <T> Sequence<T> of(T... values) {
    return of(Stream.of(values));
  }

  static <T> Sequence<T> of(Iterable<? extends T> values) {
    return new SimpleSequence<>(StreamSupport.stream(values.spliterator(), false).collect(Collectors.toList()));
  }

  static <T> Sequence<T> of(Iterator<T> values) {
    return Streamed.from(values).toSequence();
  }

  @SafeVarargs
  static <T> Sequence<T> of(Stream<? extends T>... values) {
    return Streamed.from(values).toSequence();
  }

  default Optional<T> get(int index) {
    return Optional.ofNullable(value()).filter(v -> v.size() > index).map(v -> v.get(index));
  }

  static Sequence<Void> repeat(int times) {
    return repeat(i -> null, times);
  }

  static <T> Sequence<T> repeat(IntFunction<T> factory, int times) {
    return of(IntStream.range(0, times).mapToObj(factory));
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
