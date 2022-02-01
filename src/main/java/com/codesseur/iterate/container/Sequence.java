package com.codesseur.iterate.container;

import com.codesseur.iterate.Streamed;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Sequence<T> extends CollectionContainer<T, List<T>> {

  static <T> Sequence<T> empty() {
    return of(Stream.empty());
  }

  static Sequence<Void> repeat(long times) {
    return repeat(i -> null, times);
  }

  static <T> Sequence<T> repeat(LongFunction<T> factory, long times) {
    return of(LongStream.range(0, times).mapToObj(factory));
  }

  @SafeVarargs
  static <T> Sequence<T> nonEmptyOf(Optional<T>... values) {
    return Streamed.nonEmptyOf(values).toSequence();
  }

  @SafeVarargs
  static <T> Sequence<T> nonNullOf(T... values) {
    return Streamed.nonNullOf(values).toSequence();
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

  static <T> Sequence<T> of(Iterator<? extends T> values) {
    return Streamed.of((Iterator<T>) values).toSequence();
  }

  static <T> Sequence<T> of(Stream<? extends T> values) {
    return Streamed.of((Stream<T>) values).toSequence();
  }

  @SafeVarargs
  static <T> Sequence<T> of(Stream<? extends T>... values) {
    return Streamed.of(values).toSequence();
  }

  default Optional<T> get(long index) {
    return Optional.ofNullable(value()).filter(v -> v.size() > index).map(v -> v.get((int) index));
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
