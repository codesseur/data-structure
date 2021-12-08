package com.codesseur.iterate.container;

import com.codesseur.iterate.Streamed;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
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

  default <E> E appendUnique(T value, Function<? super Streamed<T>, ? extends E> ifAppended,
      Supplier<? extends E> ifNotAppended) {
    return appendUnique(value, ifAppended, i -> ifNotAppended.get());
  }

  default <E> E appendUnique(T value, Function<? super Streamed<T>, ? extends E> ifAppended,
      Function<? super Streamed<T>, ? extends E> ifNotAppended) {
    if (notContains(value)) {
      return ifAppended.apply(append(value));
    } else {
      return ifNotAppended.apply(this);
    }
  }

  default int size() {
    return (int) count();
  }

  @Override
  default long count() {
    return value().size();
  }

  default <E> Optional<E> applyIfNotEmpty(Function<? super C, ? extends E> mapper) {
    return isNotEmpty() ? Optional.of(mapper.apply(value())) : Optional.empty();
  }
}
