package com.codesseur;

import com.codesseur.reflect.Type;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface SafeCaster {

  default <T, E> Optional<E> safeCast(T value, Type<E> type) {
    return safeCast(value, type.raw());
  }

  default <T, E> Stream<E> safeCastToStream(T value, Type<E> type) {
    return castToStream(value, type.raw());
  }

  default <T, E> Function<T, Optional<E>> safeCast(Type<E> type) {
    return safeCast(type.raw());
  }

  default <T, E> Function<T, Stream<E>> safeCastToStream(Type<E> type) {
    return safeCastToStream(type.raw());
  }

  default <T, E> Stream<E> castToStream(T value, Class<E> type) {
    return Optionals.stream(safeCast(value, type));
  }

  default <T, E> Optional<E> safeCast(T value, Class<E> type) {
    return safeCast(type).apply(value);
  }

  default <T, E> Function<T, Optional<E>> safeCast(Class<E> type) {
    return v -> Optional.ofNullable(v).filter(type::isInstance).map(type::cast);
  }

  default <T, E> Function<T, Stream<E>> safeCastToStream(Class<E> type) {
    return this.<T, E>safeCast(type).andThen(Optionals::stream);
  }
}
