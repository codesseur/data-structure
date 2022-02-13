package com.codesseur.iterate.container;

import com.codesseur.iterate.Streamed;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface Bag<T> extends CollectionContainer<T, Set<T>> {

  static <T> Bag<T> empty() {
    return of(Stream.empty());
  }

  @SafeVarargs
  static <T> Bag<T> nonEmptyOf(Optional<T>... values) {
    return Streamed.nonEmptyOf(values).toBag();
  }

  @SafeVarargs
  static <T> Bag<T> nonNullOf(T... values) {
    return Streamed.nonNullOf(values).toBag();
  }

  @SafeVarargs
  static <T> Bag<T> of(Bag<T>... values) {
    return Streamed.of(values).toBag();
  }

  @SafeVarargs
  static <T> Bag<T> of(T... values) {
    return of(Stream.of(values));
  }

  static <T> Bag<T> of(Iterable<? extends T> values) {
    return Streamed.of((Iterable<T>) values).toBag();
  }

  static <T> Bag<T> of(Iterator<? extends T> values) {
    return Streamed.of((Iterator<T>) values).toBag();
  }

  static <T> Bag<T> of(Stream<? extends T> values) {
    return Streamed.of((Stream<T>) values).toBag();
  }

  @SafeVarargs
  static <T> Bag<T> of(Stream<? extends T>... values) {
    return Streamed.of(values).toBag();
  }

  default Optional<Bag<T>> toOptional() {
    return isEmpty() ? Optional.empty() : Optional.of(this);
  }
}
