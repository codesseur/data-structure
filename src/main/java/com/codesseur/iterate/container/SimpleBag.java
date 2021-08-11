package com.codesseur.iterate.container;

import java.util.Set;

public class SimpleBag<T> extends Value<Set<T>> implements Bag<T> {

  public SimpleBag(Set<T> value) {
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
    return another instanceof Bag && value().equals(((Bag<?>) another).value());
  }

  @Override
  public String toString() {
    return "Bag{" + value().toString() + "}";
  }
}
