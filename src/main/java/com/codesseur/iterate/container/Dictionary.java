package com.codesseur.iterate.container;

import static java.util.function.Function.identity;

import com.codesseur.MicroType;
import com.codesseur.iterate.Collect;
import com.codesseur.iterate.Streamed;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Dictionary<K, V> extends MicroType<Map<K, V>> {

  static <K, V> Dictionary<K, V> empty() {
    return of(Stream.empty());
  }

  @SafeVarargs
  static <K, V> Dictionary<K, V> of(Tuple2<? extends K, ? extends V>... value) {
    return of(Stream.of(value));
  }

  static <K, V> Dictionary<K, V> of(Map<? extends K, ? extends V> value) {
    return new SimpleDictionary<>((Map<K, V>) value);
  }

  static <K, V> Dictionary<K, V> of(Stream<? extends Tuple2<? extends K, ? extends V>> value) {
    return new SimpleDictionary<>(value.collect(Collect.toMapFromTuples()));
  }

  Map<K, V> value();

  default Bag<K> keys() {
    return Bag.of(value().keySet());
  }

  default Sequence<V> values() {
    return Sequence.of(value().values());
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

  default <VV> Dictionary<K, VV> mapValue(Function<? super V, ? extends VV> valueMapper) {
    return map(identity(), valueMapper);
  }

  default <KK, VV> Dictionary<KK, VV> map(Function<? super K, ? extends KK> keyMapper,
      Function<? super V, ? extends VV> valueMapper) {
    return new SimpleDictionary<>(stream().collect(Collect.toMapFromEntries(keyMapper, valueMapper)));
  }

  default <T> Streamed<T> map(BiFunction<? super K, ? super V, ? extends T> mapper) {
    return stream().map(e -> mapper.apply(e.getKey(), e.getValue()));
  }

  default <T> Streamed<T> mapPartial(BiFunction<? super K, ? super V, ? extends Optional<T>> mapper) {
    return stream().mapPartial(e -> mapper.apply(e.getKey(), e.getValue()));
  }

  default Dictionary<K, V> remove(K key) {
    return remove(key, v -> v)._1();
  }

  default <VV> Tuple2<Dictionary<K, V>, Optional<VV>> remove(K key, Function<? super V, ? extends VV> ifOldPresent) {
    HashMap<K, V> map = new HashMap<>(value());
    V old = map.remove(key);
    return Tuple.of(new SimpleDictionary<>(map), Optional.ofNullable(old).map(ifOldPresent));
  }

  default Dictionary<K, V> replace(K key, BiFunction<? super K, ? super V, ? extends V> mapper) {
    return replace(key, mapper, (before, after) -> after)._1();
  }

  default <VV> Tuple2<Dictionary<K, V>, Optional<VV>> replace(K key, Function<? super V, ? extends V> mapper,
      BiFunction<Optional<V>, V, VV> ifReplaced) {
    return replace(key, (k, v) -> mapper.apply(v), ifReplaced);
  }

  default <VV> Tuple2<Dictionary<K, V>, Optional<VV>> replace(K key,
      BiFunction<? super K, ? super V, ? extends V> mapper,
      BiFunction<Optional<V>, V, VV> ifReplaced) {
    return get(key).map(v -> put(key, mapper.apply(key, v), ifReplaced)).orElse(Tuple.of(this, Optional.empty()));
  }

  default Dictionary<K, V> replaceIf(BiPredicate<K, V> condition,
      BiFunction<? super K, ? super V, ? extends V> mapper) {
    return map((k, v) -> condition.test(k, v) ? Tuple.of(k, mapper.apply(k, v)) : Tuple.of(k, v))
        .toDictionary(Tuple2::_1, Tuple2::_2);
  }

  default Dictionary<K, V> put(K key, V value) {
    return put(key, value, (o, n) -> n)._1();
  }

  default Dictionary<K, V> put(K key, Function<Optional<V>, V> valueMapper) {
    return put(key, valueMapper, (o, n) -> n)._1();
  }

  default <VV> Tuple2<Dictionary<K, V>, Optional<VV>> put(K key, Function<Optional<V>, V> valueMapper,
      BiFunction<Optional<V>, V, VV> ifPut) {
    return put(key, valueMapper.apply(get(key)), ifPut);
  }

  default <VV> Tuple2<Dictionary<K, V>, Optional<VV>> put(K key, V value, BiFunction<Optional<V>, V, VV> ifPut) {
    HashMap<K, V> map = new HashMap<>(value());
    V old = map.put(key, value);
    return Tuple.of(new SimpleDictionary<>(map), Optional.ofNullable(ifPut.apply(Optional.ofNullable(old), value)));
  }

  default Dictionary<K, V> filterKey(Predicate<? super K> filter) {
    return filter((k, v) -> filter.test(k));
  }

  default Dictionary<K, V> filterValue(Predicate<? super V> filter) {
    return filter((k, v) -> filter.test(v));
  }

  default Dictionary<K, V> filter(BiPredicate<? super K, ? super V> filter) {
    return new SimpleDictionary<>(stream().filter(e -> filter.test(e.getKey(), e.getValue()))
        .collect(Collect.toMapFromEntries(identity(), identity())));
  }

  default Streamed<Entry<K, V>> stream() {
    return Streamed.of(value().entrySet().stream());
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