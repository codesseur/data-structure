package com.codesseur.mixin.iterate.container;

import java.util.Objects;

public class Value<T> {

  private final T value;

  public Value(T value) {
    this.value = value;
  }

  public T value() {
    return value;
  }

  @Override
  public String toString() {
    return "Value{" + "value=" + value + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Value<?> simpleBag = (Value<?>) o;
    return Objects.equals(value, simpleBag.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
