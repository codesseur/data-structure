package com.codesseur;

import com.codesseur.iterate.Streamed;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface MicroType<T> {

  T value();

  default <E> E apply(Function<? super T, ? extends E> mapper) {
    return mapper.apply(value());
  }

  default T accept(Consumer<? super T> consumer) {
    consumer.accept(value());
    return value();
  }

  default boolean hasNoValue(T value) {
    return !hasValue(value);
  }

  default boolean hasValue(T value) {
    return matches(v -> Objects.equals(v, value));
  }

  default boolean matches(Predicate<T> predicate) {
    return predicate.test(value());
  }

  default <M extends MicroType<T>> boolean isNotEqual(M value) {
    return !equals(value);
  }

  default <M extends MicroType<T>> boolean isEqual(M value) {
    return equals(value);
  }

  default <E> Tuple2<T, E> with(E value) {
    return Tuple.of(value(), value);
  }

  default Streamed<T> toStream() {
    return Streamed.of(value());
  }

  default Optional<T> toOptional(Predicate<T> matcher) {
    return Optional.of(value()).filter(matcher);
  }

}
