package com.codesseur;

import com.codesseur.reflect.Type;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface SafeCaster {

  static <T, E> Optional<E> safeCast(T value, Type<E> type) {
    return safeCast(value, type.raw());
  }

  static <T, E> Stream<E> safeCastToStream(T value, Type<E> type) {
    return safeCastToStream(value, type.raw());
  }

  static <T, E> Function<T, Optional<E>> safeCast(Type<E> type) {
    return safeCast(type.raw());
  }

  static <T, E> Function<T, Stream<E>> safeCastToStream(Type<E> type) {
    return safeCastToStream(type.raw());
  }

  static <T, E> Stream<E> safeCastToStream(T value, Class<E> type) {
    return Optionals.stream(safeCast(value, type));
  }

  static <T, E> Optional<E> safeCast(T value, Class<E> type) {
    return safeCast(type).apply(value);
  }

  static <T, E> Function<T, Optional<E>> safeCast(Class<E> type) {
    return v -> Optional.ofNullable(v).filter(type::isInstance).map(type::cast);
  }

  static <T, E> Function<T, Stream<E>> safeCastToStream(Class<E> type) {
    return SafeCaster.<T, E>safeCast(type).andThen(Optionals::stream);
  }
}
