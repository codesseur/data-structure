package com.codesseur.iterate.container;

import static java.util.function.Function.identity;

import com.codesseur.MicroType;
import com.codesseur.iterate.Collect;
import com.codesseur.iterate.Combiner;
import com.codesseur.iterate.Streamed;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Dictionary<K, V> extends MicroType<Map<K, V>> {

  static <K, V> Dictionary<K, V> empty() {
    return of(Stream.empty());
  }

  @SafeVarargs
  static <K, V> Dictionary<K, V> of(Tuple2<? extends K, ? extends V>... value) {
    return of(Stream.of(value));
  }

  @SafeVarargs
  static <K, V> Dictionary<K, V> of(Entry<? extends K, ? extends V>... value) {
    return of(Stream.of(value).map(Tuple::fromEntry));
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

  default Dictionary<K, V> remove(Iterable<K> keys) {
    return Bag.of(keys).foldLeft(() -> this, Dictionary<K, V>::remove);
  }

  default Dictionary<K, V> remove(K key) {
    return remove(key, v -> v)._1();
  }

  default <VV> Tuple2<Dictionary<K, V>, Optional<VV>> remove(K key, Function<? super V, ? extends VV> ifOldPresent) {
    Map<K, V> map = new HashMap<>(value());
    V old = map.remove(key);
    return Tuple.of(new SimpleDictionary<>(map), Optional.ofNullable(old).map(ifOldPresent));
  }

  default Dictionary<K, V> replace(K key, V value) {
    return replace(key, v -> value);
  }

  default Dictionary<K, V> replace(K key, Function<? super V, ? extends V> mapper) {
    return replace(key, mapper, (o, v) -> null)._1;
  }

  default <VV> Tuple2<Dictionary<K, V>, Optional<VV>> replace(K key, Function<? super V, ? extends V> mapper,
      BiFunction<V, V, VV> ifReplaced) {
    return putMaybe(key, o -> o.map(mapper), (o, v) -> o.map(old -> ifReplaced.apply(old, v)).orElse(null));
  }

  default Dictionary<K, V> replaceIf(Predicate<V> condition, Function<? super V, ? extends V> mapper) {
    return replaceIf((k, v) -> condition.test(v), (k, v) -> mapper.apply(v));
  }

  default Dictionary<K, V> replaceIf(BiPredicate<K, V> condition, Function<? super V, ? extends V> mapper) {
    return replaceIf(condition, (k, v) -> mapper.apply(v));
  }

  default Dictionary<K, V> replaceIf(BiPredicate<K, V> condition,
      BiFunction<? super K, ? super V, ? extends V> mapper) {
    return map((k, v) -> condition.test(k, v) ? Tuple.of(k, mapper.apply(k, v)) : Tuple.of(k, v))
        .toDictionary(Tuple2::_1, Tuple2::_2);
  }

  default Dictionary<K, V> put(K key, V value) {
    return put(key, o -> value);
  }

  default Dictionary<K, V> put(K key, Function<? super V, V> ifExistsMapper, Supplier<V> ifNotExistsSupplier) {
    return put(key, o -> o.map(ifExistsMapper).orElseGet(ifNotExistsSupplier));
  }

  default Dictionary<K, V> put(K key, Function<Optional<V>, V> valueMapper) {
    return putMaybe(key, o -> Optional.ofNullable(valueMapper.apply(o)));
  }

  default Dictionary<K, V> putMaybe(K key, Function<? super Optional<V>, ? extends Optional<V>> valueMapper) {
    return putMaybe(key, valueMapper, (o, n) -> null)._1;
  }

  default <VV> Tuple2<Dictionary<K, V>, Optional<VV>> putMaybe(K key,
      Function<? super Optional<V>, ? extends Optional<V>> valueMapper,
      BiFunction<? super Optional<V>, ? super V, VV> onPut) {
    return valueMapper.apply(get(key))
        .map(v -> {
          Map<K, V> map = new HashMap<>(value());
          V old = map.put(key, v);
          return Tuple.of(SimpleDictionary.from(map), Optional.ofNullable(onPut.apply(Optional.ofNullable(old), v)));
        }).orElseGet(() -> Tuple.of(this, Optional.empty()));
  }

  default Dictionary<K, V> put(Function<? super V, ? extends K> keyExtractor, Iterable<? extends V> values) {
    return put((Map<K, V>) Streamed.of(values).toMap(keyExtractor));
  }

  default Dictionary<K, V> put(Dictionary<K, V> values) {
    return put(values.value());
  }

  default Dictionary<K, V> put(Map<K, V> values) {
    Map<K, V> map = new HashMap<>(value());
    map.putAll(values);
    return Dictionary.of(map);
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

  default <VV> Combiner<Entry<K, V>, Entry<K, VV>, Entry<K, V>, Entry<K, VV>, K> joinByKey(
      Dictionary<K, VV> dictionary) {
    return stream().join(dictionary.stream()).by(Entry::getKey, Entry::getKey);
  }

  default Dictionary<K, V> merge(Dictionary<K, V> dictionary) {
    return new SimpleDictionary<>(
        Stream.concat(stream(), dictionary.stream())
            .collect(Collect.toMapFromEntries(identity(), identity())));
  }

  default void forEachKey(Consumer<K> consumer) {
    forEach((k, v) -> consumer.accept(k));
  }

  default void forEachValue(Consumer<V> consumer) {
    forEach((k, v) -> consumer.accept(v));
  }

  default void forEach(BiConsumer<K, V> consumer) {
    value().forEach(consumer);
  }

  default boolean isEmpty() {
    return value().isEmpty();
  }

  default boolean isNotEmpty() {
    return !isEmpty();
  }

}
