package com.codesseur.mixin.iterate.container;

import com.codesseur.mixin.Optionals;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.codesseur.mixin.iterate.Streamed;
import java.util.stream.StreamSupport;

public interface Bag<T> extends CollectionContainer<T, Set<T>> {

  static <T> Bag<T> empty() {
    return of(Stream.empty());
  }

  @SafeVarargs
  static <T> Bag<T> noEmptyOf(Optional<T>... values) {
    return of(Stream.of(values).flatMap(Optionals::stream));
  }

  @SafeVarargs
  static <T> Bag<T> nonNullOf(T... values) {
    return of(Stream.of(values).filter(Objects::nonNull));
  }

  @SafeVarargs
  static <T> Bag<T> of(Bag<T>... values) {
    return of(Stream.of(values).flatMap(Container::stream));
  }

  @SafeVarargs
  static <T> Bag<T> of(T... values) {
    return of(Stream.of(values));
  }

  static <T> Bag<T> of(Iterable<? extends T> values) {
    return new SimpleBag<>(StreamSupport.stream(values.spliterator(), false).collect(Collectors.toSet()));
  }

  static <T> Bag<T> of(Iterator<T> values) {
    return Streamed.from(values).toBag();
  }

  @SafeVarargs
  static <T> Bag<T> of(Stream<? extends T>... values) {
    return Streamed.from(values).toBag();
  }

  default Optional<Bag<T>> toOptional() {
    return isEmpty() ? Optional.empty() : Optional.of(this);
  }
}
