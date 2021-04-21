package com.codesseur.mixin.iterate.container;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface CollectionContainer<T, C extends Collection<T>> extends Container<T, C> {

  default Stream<T> stream() {
    return value().stream();
  }

  default boolean isEmpty() {
    return value().isEmpty();
  }

  default boolean isNotEmpty() {
    return !isEmpty();
  }

  default boolean contains(T element) {
    return value().contains(element);
  }

  default boolean notContains(T element) {
    return !contains(element);
  }

  default <K> boolean containsBy(Function<? super T, ? extends K> extractor, K key) {
    return anyMatch(t -> extractor.apply(t).equals(key));
  }

  default <K> boolean notContainsBy(Function<? super T, ? extends K> extractor, K key) {
    return !containsBy(extractor, key);
  }

  default int size() {
    return value().size();
  }

  default <E> Optional<E> applyIfNotEmpty(Function<? super C, ? extends E> mapper) {
    return isNotEmpty() ? Optional.of(mapper.apply(value())) : Optional.empty();
  }
}
