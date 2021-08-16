package com.codesseur.iterate;

import static java.util.Objects.requireNonNull;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

import com.codesseur.Optionals;
import com.codesseur.functions.Unchecks;
import com.codesseur.iterate.container.Bag;
import com.codesseur.iterate.container.Dictionary;
import com.codesseur.iterate.container.Sequence;
import io.vavr.CheckedFunction1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Streamed<T> extends Stream<T>, Iterable<T> {

  Stream<T> stream();

  @SafeVarargs
  static <T> Streamed<T> of(T... values) {
    return of(Stream.of(values));
  }

  static <T> Streamed<T> of(Iterator<? extends T> values) {
    requireNonNull(values);
    return () -> StreamSupport.stream(spliteratorUnknownSize(values, ORDERED), false);
  }

  static <T> Streamed<T> of(Iterable<? extends T> values) {
    requireNonNull(values);
    return of(StreamSupport.stream(values.spliterator(), false));
  }

  @SafeVarargs
  static <T> Streamed<T> of(Stream<? extends T>... values) {
    Stream<T> ts = Stream.of(values).flatMap(identity());
    return () -> ts;
  }

  static <T> Streamed<T> empty() {
    return of(Collections.emptyList());
  }

  @Override
  default Streamed<T> filter(Predicate<? super T> predicate) {
    return Streamed.of(stream().filter(predicate));
  }

  @Override
  default <R> Streamed<R> map(Function<? super T, ? extends R> mapper) {
    return Streamed.of(stream().map(mapper));
  }

  @Override
  default IntStream mapToInt(ToIntFunction<? super T> mapper) {
    return stream().mapToInt(mapper);
  }

  @Override
  default LongStream mapToLong(ToLongFunction<? super T> mapper) {
    return stream().mapToLong(mapper);
  }

  @Override
  default DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
    return stream().mapToDouble(mapper);
  }

  @Override
  default <R> Streamed<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
    return of(stream().flatMap(mapper));
  }

  @Override
  default IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
    return stream().flatMapToInt(mapper);
  }

  @Override
  default LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
    return stream().flatMapToLong(mapper);
  }

  @Override
  default DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
    return stream().flatMapToDouble(mapper);
  }

  @Override
  default Streamed<T> distinct() {
    return of(stream().distinct());
  }

  @Override
  default Streamed<T> sorted() {
    return of(stream().sorted());
  }

  @Override
  default Streamed<T> sorted(Comparator<? super T> comparator) {
    return of(stream().sorted(comparator));
  }

  @Override
  default Streamed<T> peek(Consumer<? super T> action) {
    return of(stream().peek(action));
  }

  @Override
  default Streamed<T> limit(long maxSize) {
    return of(stream().limit(maxSize));
  }

  @Override
  default Streamed<T> skip(long n) {
    return of(stream().skip(n));
  }

  @Override
  default void forEach(Consumer<? super T> action) {
    stream().forEach(action);
  }

  @Override
  default void forEachOrdered(Consumer<? super T> action) {
    stream().forEachOrdered(action);
  }

  @Override
  default Object[] toArray() {
    return stream().toArray();
  }

  @Override
  default <A> A[] toArray(IntFunction<A[]> generator) {
    return stream().toArray(generator);
  }

  @Override
  default T reduce(T identity, BinaryOperator<T> accumulator) {
    return stream().reduce(identity, accumulator);
  }

  @Override
  default Optional<T> reduce(BinaryOperator<T> accumulator) {
    return stream().reduce(accumulator);
  }

  @Override
  default <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
    return stream().reduce(identity, accumulator, combiner);
  }

  @Override
  default <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
    return stream().collect(supplier, accumulator, combiner);
  }

  @Override
  default <R, A> R collect(Collector<? super T, A, R> collector) {
    return stream().collect(collector);
  }

  @Override
  default Optional<T> min(Comparator<? super T> comparator) {
    return stream().min(comparator);
  }

  @Override
  default Optional<T> max(Comparator<? super T> comparator) {
    return stream().max(comparator);
  }

  @Override
  default long count() {
    return stream().count();
  }

  @Override
  default boolean anyMatch(Predicate<? super T> predicate) {
    return stream().anyMatch(predicate);
  }

  @Override
  default boolean allMatch(Predicate<? super T> predicate) {
    return stream().allMatch(predicate);
  }

  @Override
  default boolean noneMatch(Predicate<? super T> predicate) {
    return stream().noneMatch(predicate);
  }

  @Override
  default Optional<T> findFirst() {
    return stream().findFirst();
  }

  @Override
  default Optional<T> findAny() {
    return stream().findAny();
  }

  @Override
  default Iterator<T> iterator() {
    return stream().iterator();
  }

  @Override
  default Spliterator<T> spliterator() {
    return stream().spliterator();
  }

  @Override
  default boolean isParallel() {
    return stream().isParallel();
  }

  @Override
  default Streamed<T> sequential() {
    return of(stream().sequential());
  }

  @Override
  default Streamed<T> parallel() {
    return of(stream().parallel());
  }

  @Override
  default Streamed<T> unordered() {
    return of(stream().unordered());
  }

  @Override
  default Streamed<T> onClose(Runnable closeHandler) {
    return of(stream().onClose(closeHandler));
  }

  @Override
  default void close() {
    stream().close();
  }

  default Optional<T> head() {
    return findFirst();
  }

  default Streamed<T> tail() {
    return skip(1);
  }

  default <E> Streamed<Tuple2<T, E>> flatMapSticky(Function<? super T, ? extends Stream<E>> mapper) {
    requireNonNull(mapper);
    return flatMap(e1 -> mapper.apply(e1).map(e -> Tuple.of(e1, e)));
  }

  default <E> Streamed<E> mapTryOtherwise(CheckedFunction1<? super T, ? extends E> mapper,
      Function<? super Throwable, ? extends E> otherwise) {
    requireNonNull(mapper);
    requireNonNull(otherwise);
    return map(v -> {
      try {
        return mapper.apply(v);
      } catch (Throwable t) {
        return otherwise.apply(t);
      }
    });
  }

  default <E> Streamed<E> mapTry(CheckedFunction1<? super T, ? extends E> mapper) {
    requireNonNull(mapper);
    return map(Unchecks.Func.uncheck(mapper));
  }

  default Streamed<T> mapLast(Function<? super T, ? extends T> last) {
    requireNonNull(last);
    return mapLastOtherwise(last, identity());
  }

  default <E> Streamed<E> mapLastOtherwise(Function<? super T, ? extends E> last,
      Function<? super T, ? extends E> other) {
    requireNonNull(last);
    requireNonNull(other);
    return map(other, other, last, MappingPriority.LAST);
  }

  default Streamed<T> mapFirst(Function<? super T, ? extends T> first) {
    return mapFirstOtherwise(first, identity());
  }

  default <E> Streamed<E> mapFirstOtherwise(Function<? super T, ? extends E> first,
      Function<? super T, ? extends E> other) {
    return map(first, other, other, MappingPriority.FIRST);
  }

  default <E> Streamed<E> map(Function<? super T, ? extends E> first,
      Function<? super T, ? extends E> middle,
      Function<? super T, ? extends E> last) {
    return map(first, middle, last, MappingPriority.FIRST);
  }

  default <E> Streamed<E> map(Function<? super T, ? extends E> first,
      Function<? super T, ? extends E> middle,
      Function<? super T, ? extends E> last,
      MappingPriority mappingPriority) {
    return zipWithIndex().map(i -> mappingPriority.handle(i, first, middle, last));
  }

  default <E> Streamed<E> flatMapIterable(Function<? super T, ? extends Iterable<E>> mapper) {
    return map(mapper).flatMap(v -> StreamSupport.stream(v.spliterator(), false));
  }

  default <E> Streamed<E> mapPartial(Function<? super T, ? extends Optional<E>> mapper) {
    return map(mapper).flatMap(Optionals::stream);
  }

  default Streamed<T> replaceIf(Predicate<T> condition, T value) {
    return replaceIf(condition, i -> value);
  }

  default Streamed<T> replaceIf(Predicate<T> condition, Function<? super T, ? extends T> mapper) {
    return replaceIfOr(condition, mapper, () -> this);
  }

  default Streamed<T> replaceIfOrAppend(Predicate<T> condition, Function<? super T, ? extends T> mapper,
      Supplier<? extends T> append) {
    return replaceIfOr(condition, mapper, () -> append(append.get()));
  }

  default Streamed<T> replaceIfOr(Predicate<T> condition, Function<? super T, ? extends T> mapper,
      Supplier<? extends Streamed<T>> otherwise) {
    AtomicBoolean found = new AtomicBoolean(false);
    Streamed<T> ts = map(v -> {
      if (condition.test(v)) {
        found.set(true);
        return mapper.apply(v);
      }
      return v;
    });
    return found.get() ? ts : otherwise.get();
  }

  default Streamed<T> removeIf(Predicate<T> condition) {
    return filter(condition.negate());
  }

  default Streamed<T> take(long offset, long length) {
    return skip(offset).limit(length);
  }

  default Streamed<T> distinctBy(Function<? super T, ?> keyExtractor, BinaryOperator<T> duplicateResolver) {
    return of(collect(groupingBy(keyExtractor, reducing(duplicateResolver))).values()).flatMap(Optionals::stream);
  }

  default Streamed<T> distinctBy(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return filter(t -> seen.add(keyExtractor.apply(t)));
  }

  default <E, I extends Iterable<E>> Streamed<Tuple2<T, E>> innerZip(I second) {
    return zip(second, ZipMode.INTERSECT)
        .map(IndexedValue::value)
        .map(p -> p.apply(Optionals::and))
        .flatMap(Optionals::stream);
  }

  default <E, I extends Iterable<E>> Streamed<Tuple2<Optional<T>, Optional<E>>> outerZip(I second) {
    return zip(second, ZipMode.UNION).map(IndexedValue::value);
  }

  default <E, I extends Iterable<E>> Streamed<Tuple2<T, Optional<E>>> leftOuterZip(I second) {
    return zip(second, ZipMode.UNION).map(IndexedValue::value).mapPartial(t -> t._1().map(v -> t.map1(i -> v)));
  }

  default <E, I extends Iterable<E>> Streamed<Tuple2<Optional<T>, E>> rightOuterZip(I second) {
    return zip(second, ZipMode.UNION).map(IndexedValue::value).mapPartial(t -> t._2().map(v -> t.map2(i -> v)));
  }

  default Streamed<IndexedValue<T>> zipWithIndex() {
    return zip(Collections.emptyList(), ZipMode.UNION)
        .map(i -> i.map(Tuple2::_1).map(o -> o.orElse(null)));
  }

  default <E, I extends Iterable<E>> Streamed<IndexedValue<Tuple2<Optional<T>, Optional<E>>>> zip(I second,
      ZipMode zipMode) {
    Iterable<IndexedValue<Tuple2<Optional<T>, Optional<E>>>> iterable = () -> new ZippedIterator<>(stream().iterator(),
        second.iterator(), zipMode);
    return of(StreamSupport.stream(iterable.spliterator(), false));
  }

  default boolean contains(T element) {
    return contains(e -> e.equals(element));
  }

  default boolean contains(Predicate<? super T> condition) {
    return anyMatch(condition);
  }

  default Optional<T> find(Predicate<? super T> condition) {
    return filter(condition).findFirst();
  }

  default <E> E foldLeft(E seed, BiFunction<E, ? super T, E> accumulator) {
    return reduce(seed, accumulator, (v1, v2) -> v1);
  }

  default Optional<T> foldLeft(BinaryOperator<T> accumulator) {
    return reduce(accumulator);
  }

  default <E> E foldRight(E seed, BiFunction<? super T, E, E> accumulator) {
    E result = seed;
    final List<T> list = toList();
    for (int i = list.size() - 1; i >= 0; i--) {
      result = accumulator.apply(list.get(i), result);
    }
    return result;
  }

  default Optional<T> foldRight(BinaryOperator<T> accumulator) {
    return foldRight(Optional.empty(), (t1, e) -> e.map(v -> accumulator.apply(t1, v)));
  }

  default <TT, I extends Iterable<TT>> Joiner<T, TT> join(I iterable) {
    return new Joiner<>(this, Streamed.of(iterable));
  }

  default <TT> Streamed<T> project(Set<TT> keys, Function<? super T, ? extends TT> extractor) {
    return filter(e -> keys.contains(extractor.apply(e)));
  }

  default <R, A, I extends Iterable<T>> R minus(I iterable, Collector<? super T, A, R> collector) {
    return minusBy(iterable, identity(), collector);
  }

  default <I extends Iterable<T>> Streamed<T> minus(I iterable) {
    return minusBy(iterable, identity());
  }

  default <R, A, I extends Iterable<T>> R minusBy(I iterable, Function<? super T, ?> by,
      Collector<? super T, A, R> collector) {
    return minusBy(iterable, by).collect(collector);
  }

  default <I extends Iterable<T>> Streamed<T> minusBy(I iterable, Function<? super T, ?> by) {
    Set<?> keys = Streamed.of(iterable).map(by).collect(Collectors.toSet());
    return of(stream().filter(e -> !keys.contains(by.apply(e))));
  }

  default Optional<T> last() {
    return reduce((e1, e2) -> e2);
  }

  default Streamed<T> addAt(T value, int index) {
    return zipWithIndex()
        .flatMap(p -> p.isIndexed(index) ? Stream.of(p.value(), value) : Stream.of(p.value()));
  }

  default Streamed<T> append(T value) {
    return append(Collections.singletonList(value));
  }

  default Streamed<T> append(T... values) {
    return append(Arrays.asList(values));
  }

  default <I extends Iterable<T>> Streamed<T> append(I values) {
    return Streamed.of(this, Streamed.of(values));
  }

  default Streamed<T> prepend(T value) {
    return prepend(Collections.singletonList(value));
  }

  default Streamed<T> prepend(T... values) {
    return prepend(Arrays.asList(values));
  }

  default <I extends Iterable<T>> Streamed<T> prepend(I values) {
    return Streamed.of(Streamed.of(values), this);
  }

  default Streamed<Optional<T>> toOptionals() {
    return map(Optional::ofNullable);
  }

  default <S extends Sequence<T>> S toSequence(Function<? super List<T>, ? extends S> factory) {
    return collect(Collect.toSequence(factory));
  }

  default <S extends Bag<T>> S toBag(Function<? super Set<T>, ? extends S> factory) {
    return collect(Collect.toBag(factory));
  }

  default Sequence<T> toSequence() {
    return collect(Collect.toSequence());
  }

  default Bag<T> toBag() {
    return collect(Collect.toBag());
  }

  default <S> S toSetThen(Function<? super Set<T>, ? extends S> factory) {
    return factory.apply(toSet());
  }

  default Set<T> toSet() {
    return collect(Collectors.toSet());
  }

  default <S extends Sequence<T>> S toListThen(Function<? super List<T>, ? extends S> factory) {
    return factory.apply(toList());
  }

  default List<T> toList() {
    return collect(Collectors.toList());
  }

  default <K> Map<K, T> toMap(Function<? super T, ? extends K> keyExtractor) {
    return toMap(keyExtractor, Function.identity());
  }

  default <K, V> Map<K, V> toMap(Function<? super T, ? extends K> keyExtractor,
      Function<? super T, ? extends V> valueExtractor) {
    return collect(Collect.toMap(keyExtractor, valueExtractor));
  }

  default <K> Dictionary<K, T> toDictionary(Function<? super T, ? extends K> keyExtractor) {
    return Dictionary.of(toMap(keyExtractor));
  }

  default <K, V> Dictionary<K, V> toDictionary(Function<? super T, ? extends K> keyExtractor,
      Function<? super T, ? extends V> valueExtractor) {
    return Dictionary.of(toMap(keyExtractor, valueExtractor));
  }

  default <C> C collect(Function<? super Iterable<? extends T>, ? extends C> collector) {
    return collector.apply(toList());
  }

  default <K> Dictionary<K, Streamed<T>> groupBy(Function<? super T, ? extends K> classifier) {
    return groupBy(classifier, identity());
  }

  default <K, E> Dictionary<K, E> groupBy(Function<? super T, ? extends K> classifier,
      Function<Streamed<T>, E> mapper) {
    Map<? extends K, E> collect = collect(
        groupingBy(classifier, collectingAndThen(Collectors.toList(), l -> mapper.apply(Streamed.of(l)))));
    return Dictionary.of(collect);
  }

}
