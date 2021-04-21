package com.codesseur.mixin.iterate.container;

import com.codesseur.mixin.iterate.Streamed;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface MultiValuedDictionary<K, V> extends Dictionary<K, Sequence<V>> {

  static <K, V> MultiValuedDictionary<K, V> empty() {
    return of(Stream.empty());
  }

  static <K, V> MultiValuedDictionary<K, V> of(Map<K, List<V>> values) {
    return of(values.entrySet().stream().flatMap(e -> e.getValue().stream().map(v -> Tuple.of(e.getKey(), v))));
  }

  @SafeVarargs
  static <K, V> MultiValuedDictionary<K, V> of(Tuple2<K, V>... values) {
    return of(Stream.of(values));
  }

  static <K, V> MultiValuedDictionary<K, V> of(Stream<Tuple2<K, V>> values) {
    final Map<K, Sequence<V>> map = values.map(p -> p.map2(Sequence::of))
        .collect(Collectors.toMap(Tuple2::_1, Tuple2::_2, (l1, l2) -> l2));
    return new SimpleMultiValuedDictionary<>(map);
  }

  default Optional<V> getSingle(K key) {
    return get(key).flatMap(Streamed::findFirst);
  }

  default Sequence<V> getMulti(K key) {
    return get(key).orElseGet(Sequence::empty);
  }
}
