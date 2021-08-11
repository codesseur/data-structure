package com.codesseur.iterate.container;

import java.util.List;

public class SimpleSequence<T> extends Value<List<T>> implements Sequence<T> {

  public SimpleSequence(List<T> value) {
    super(value);
  }

  @Override
  public int hashCode() {
    return value().hashCode();
  }

  @Override
  public boolean equals(Object another) {
    if (this == another) {
      return true;
    }
    return another instanceof Sequence && value().equals(((Sequence<?>) another).value());
  }

  @Override
  public String toString() {
    return "Sequence{" + value().toString() + "}";
  }
}
