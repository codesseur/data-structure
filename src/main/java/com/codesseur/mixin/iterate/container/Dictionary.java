package com.codesseur.mixin.iterate.container;

import static java.util.function.Function.identity;

import com.codesseur.mixin.iterate.Collect;
import com.codesseur.mixin.iterate.Streamed;
import io.vavr.Tuple2;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import com.codesseur.mixin.MicroType;

public interface Dictionary<K, V> extends MicroType<Map<K, V>> {

  static <K, V> Dictionary<K, V> empty() {
    return of(Stream.empty());
  }

  @SafeVarargs
  static <K, V> Dictionary<K, V> of(Tuple2<? extends K, ? extends V>... value) {
    return of(Stream.of(value));
  }

  static <K, V> Dictionary<K, V> of(Map<? extends K, ? extends V> value) {
    return () -> (Map<K, V>) value;
  }

  static <K, V> Dictionary<K, V> of(Stream<? extends Tuple2<? extends K, ? extends V>> value) {
    Map<K, V> map = value.collect(Collect.toMapFromTuples());
    return () -> map;
  }

  Map<K, V> value();

  default Bag<K> keys() {
    return Bag.of(value().keySet());
  }

  default boolean hasKey(K key) {
    return value().containsKey(key);
  }

  default int size() {
    return value().size();
  }

  default Optional<V> get(K key) {
    return Optional.ofNullable(value().get(key));
  }

  default <KK> Dictionary<KK, V> mapKey(Function<? super K, ? extends KK> keyMapper) {
    return map(keyMapper, identity());
  }

  default <VV> Dictionary<K, VV> mapValue(Function<? super V, ? extends VV> keyMapper) {
    return map(identity(), keyMapper);
  }

  default <KK, VV> Dictionary<KK, VV> map(Function<? super K, ? extends KK> keyMapper,
      Function<? super V, ? extends VV> valueMapper) {
    return () -> stream().collect(Collect.toMapFromEntries(keyMapper, valueMapper));
  }

  default <T> Streamed<T> map(BiFunction<? super K, ? super V, ? extends T> mapper) {
    return stream().map(e -> mapper.apply(e.getKey(), e.getValue()));
  }

  default <T> Streamed<T> mapPartial(BiFunction<? super K, ? super V, ? extends Optional<T>> mapper) {
    return stream().mapPartial(e -> mapper.apply(e.getKey(), e.getValue()));
  }

  default Dictionary<K, V> filterKey(Predicate<? super K> filter) {
    return filter((k, v) -> filter.test(k));
  }

  default Dictionary<K, V> filterValue(Predicate<? super V> filter) {
    return filter((k, v) -> filter.test(v));
  }

  default Dictionary<K, V> filter(BiPredicate<? super K, ? super V> filter) {
    return () -> stream().filter(e -> filter.test(e.getKey(), e.getValue()))
        .collect(Collect.toMapFromEntries(identity(), identity()));
  }

  default Streamed<Entry<K, V>> stream() {
    return Streamed.from(value().entrySet().stream());
  }

  default Dictionary<K, V> merge(Dictionary<K, V> dictionary) {
    return new SimpleDictionary<>(
        Stream.concat(stream(), dictionary.stream())
            .collect(Collect.toMapFromEntries(identity(), identity())));
  }

  default boolean isEmpty() {
    return value().isEmpty();
  }

  default boolean isNotEmpty() {
    return !isEmpty();
  }
}
