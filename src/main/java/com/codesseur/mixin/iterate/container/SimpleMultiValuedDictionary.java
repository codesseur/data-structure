package com.codesseur.mixin.iterate.container;

import java.util.Map;

public class SimpleMultiValuedDictionary<K, V> extends Value<Map<K, Sequence<V>>> implements
    MultiValuedDictionary<K, V> {

  public SimpleMultiValuedDictionary(Map<K, Sequence<V>> value) {
    super(value);
  }
}
