package com.codesseur.mixin.iterate.container;

import java.util.Map;

public class SimpleDictionary<K, V> extends Value<Map<K, V>> implements Dictionary<K, V> {

  public SimpleDictionary(Map<K, V> value) {
    super(value);
  }
}
