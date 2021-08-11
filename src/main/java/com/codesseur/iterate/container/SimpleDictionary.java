package com.codesseur.iterate.container;

import java.util.Map;

public class SimpleDictionary<K, V> extends Value<Map<K, V>> implements Dictionary<K, V> {

  public SimpleDictionary(Map<K, V> value) {
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
    return another instanceof Dictionary && value().equals(((Dictionary<?, ?>) another).value());
  }

  @Override
  public String toString() {
    return "Dictionary{" + value().toString() + "}";
  }
}
