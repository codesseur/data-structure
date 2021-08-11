package com.codesseur.iterate.container;

import java.util.Map;

public class SimpleMultiValuedDictionary<K, V> extends Value<Map<K, Sequence<V>>> implements
    MultiValuedDictionary<K, V> {

  public SimpleMultiValuedDictionary(Map<K, Sequence<V>> value) {
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
    return another instanceof MultiValuedDictionary && value().equals(((MultiValuedDictionary<?, ?>) another).value());
  }

  @Override
  public String toString() {
    return "MultiDictionary{" + value().toString() + "}";
  }
}
