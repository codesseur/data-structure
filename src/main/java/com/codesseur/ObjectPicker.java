package com.codesseur;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ObjectPicker {

  public static <T> Pick<T> pick(T value) {
    return new Pick<>(value);
  }

  public static <T, O extends Optional<T>> OptionalPick<T> pick(O value) {
    return new OptionalPick<>(value);
  }

  public static class OptionalPick<T> {

    private final Optional<T> value;

    OptionalPick(Optional<T> value) {
      this.value = value;
    }

    public <E> Optional<Tuple2<T, E>> with(E value) {
      return this.value.map(v -> Tuple.of(v, value));
    }

    public Optional<Nothing> ifEmptyOr(Predicate<T> condition) {
      return this.value.map(condition::test).orElse(true) ? Optional.of(Nothing.NOTHING) : Optional.empty();
    }

    public Optional<T> peek(Consumer<T> consumer) {
      return this.value.map(v -> {
        consumer.accept(v);
        return v;
      });
    }

  }

  public static class Pick<T> implements MicroType<T> {

    private final T value;

    Pick(T value) {
      this.value = value;
    }

    public <E> Optional<Tuple2<T, E>> with(Optional<E> value) {
      return value.map(v -> Tuple.of(this.value, v));
    }

    public T replaceIf(Predicate<T> condition, UnaryOperator<T> mapper) {
      if (condition.test(value)) {
        return mapper.apply(value);
      } else {
        return value;
      }
    }

    public T peek(Consumer<T> consumer) {
      consumer.accept(value);
      return value;
    }

    @Override
    public T value() {
      return value;
    }
  }
}
