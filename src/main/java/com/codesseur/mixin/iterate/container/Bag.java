package com.codesseur.mixin.iterate.container;

import com.codesseur.mixin.Optionals;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import com.codesseur.mixin.iterate.Streamed;

public interface Bag<T> extends CollectionContainer<T, Set<T>> {

  static <T> Bag<T> empty() {
    return of(Stream.empty());
  }

  @SafeVarargs
  static <T> Bag<T> of(Bag<T>... values) {
    return of(Stream.of(values).flatMap(Container::stream));
  }

  @SafeVarargs
  static <T> Bag<T> of(T... values) {
    return of(Stream.of(values));
  }

  @SafeVarargs
  static <T> Bag<T> of(Optional<T>... values) {
    return of(Stream.of(values).flatMap(Optionals::stream));
  }

  @SafeVarargs
  static <T> Bag<Optional<T>> ofNullables(T... values) {
    return of(Stream.of(values).map(Optional::ofNullable));
  }

  static <T> Bag<T> of(Set<? extends T> values) {
    return new SimpleBag<>((Set<T>) values);
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
