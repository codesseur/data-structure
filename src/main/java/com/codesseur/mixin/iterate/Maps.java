package com.codesseur.mixin.iterate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Maps {

  @SafeVarargs
  public static <K, V> Map<K, V> combine(Map<? extends K, ? extends V>... maps) {
    Map<K, V> map = new HashMap<>();
    Stream.of(maps).forEach(map::putAll);
    return map;
  }
}
