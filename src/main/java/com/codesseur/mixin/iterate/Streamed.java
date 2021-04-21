package com.codesseur.mixin.iterate;

import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

import com.codesseur.mixin.Optionals;
import com.codesseur.mixin.iterate.container.Bag;
import com.codesseur.mixin.iterate.container.Sequence;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
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
import com.codesseur.mixin.iterate.container.Dictionary;

public interface Streamed<T> extends Stream<T>, Iterable<T> {

  Stream<T> stream();

  @SafeVarargs
  static <T> Streamed<T> from(T... values) {
    return from(Stream.of(values));
  }

  static <T> Streamed<T> from(Iterator<? extends T> values) {
    return () -> StreamSupport.stream(spliteratorUnknownSize(values, ORDERED), false);
  }

  static <T> Streamed<T> from(Collection<? extends T> values) {
    return from(values.stream());
  }

  @SafeVarargs
  static <T> Streamed<T> from(Stream<? extends T>... values) {
    Stream<T> ts = Stream.of(values).reduce(Stream::concat).orElseGet(Stream::empty).map(t -> (T) t);
    return () -> ts;
  }

  @Override
  default Streamed<T> filter(Predicate<? super T> predicate) {
    return Streamed.from(stream().filter(predicate));
  }

  @Override
  default <R> Streamed<R> map(Function<? super T, ? extends R> mapper) {
    return Streamed.from(stream().map(mapper));
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
    return from(stream().flatMap(mapper));
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
    return from(stream().distinct());
  }

  @Override
  default Streamed<T> sorted() {
    return from(stream().sorted());
  }

  @Override
  default Streamed<T> sorted(Comparator<? super T> comparator) {
    return from(stream().sorted(comparator));
  }

  @Override
  default Streamed<T> peek(Consumer<? super T> action) {
    return from(stream().peek(action));
  }

  @Override
  default Streamed<T> limit(long maxSize) {
    return from(stream().limit(maxSize));
  }

  @Override
  default Streamed<T> skip(long n) {
    return from(stream().skip(n));
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
    return from(stream().sequential());
  }

  @Override
  default Streamed<T> parallel() {
    return from(stream().parallel());
  }

  @Override
  default Streamed<T> unordered() {
    return from(stream().unordered());
  }

  @Override
  default Streamed<T> onClose(Runnable closeHandler) {
    return from(stream().onClose(closeHandler));
  }

  @Override
  default void close() {
    stream().close();
  }

  default <E> Streamed<Tuple2<T, E>> flatMapExtract(Function<? super T, ? extends Stream<E>> first) {
    return flatMap(e1 -> first.apply(e1).map(e -> Tuple.of(e1, e)));
  }

  default <E> Streamed<E> mapLastOtherwise(Function<? super T, ? extends E> last,
      Function<? super T, ? extends E> others) {
    return map(others, others, last, MappingPriority.LAST);
  }

  default <E> Streamed<E> mapFirstOtherwise(Function<? super T, ? extends E> first,
      Function<? super T, ? extends E> others) {
    return map(first, others, others, MappingPriority.FIRST);
  }

  default <E> Streamed<E> map(Function<? super T, ? extends E> first, Function<? super T, ? extends E> middle,
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
    return map(v -> condition.test(v) ? mapper.apply(v) : v);
  }

  default Streamed<T> removeIf(Predicate<T> condition) {
    return filter(condition.negate());
  }

  default Streamed<T> take(long offset, long length) {
    return skip(offset).limit(length);
  }

  default Streamed<T> distinctBy(Function<? super T, ?> keyExtractor, BinaryOperator<T> duplicateResolver) {
    return from(collect(groupingBy(keyExtractor, reducing(duplicateResolver))).values())
        .flatMap(Optionals::stream);
  }

  default Streamed<T> distinctBy(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return filter(t -> seen.add(keyExtractor.apply(t)));
  }

  default <E, S extends Stream<E>> Streamed<Tuple2<T, E>> innerZip(S second) {
    return zip(second, ZipMode.INTERSECT)
        .map(IndexedValue::value)
        .map(p -> p.apply(Optionals::and))
        .flatMap(Optionals::stream);
  }

  default <E, S extends Stream<E>> Streamed<Tuple2<Optional<T>, Optional<E>>> outerZip(S second) {
    return zip(second, ZipMode.UNION).map(IndexedValue::value);
  }

  default Streamed<IndexedValue<T>> zipWithIndex() {
    return zip(Stream.empty(), ZipMode.UNION)
        .map(i -> i.map(Tuple2::_1).map(o -> o.orElse(null)));
  }

  default <E, S extends Stream<E>> Streamed<IndexedValue<Tuple2<Optional<T>, Optional<E>>>> zip(S second,
      ZipMode zipMode) {
    Iterable<IndexedValue<Tuple2<Optional<T>, Optional<E>>>> iterable = () -> new ZippedIterator<>(stream().iterator(),
        second.iterator(), zipMode);
    return from(StreamSupport.stream(iterable.spliterator(), false));
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

  default <TT, S extends Stream<TT>> Joiner<T, TT> join(S stream) {
    return new Joiner<>(this, stream);
  }

  default <TT> Streamed<T> project(Set<TT> keys, Function<? super T, ? extends TT> extractor) {
    return filter(e -> keys.contains(extractor.apply(e)));
  }

  default <R> R to(Function<? super Streamed<T>, ? extends R> converter) {
    return converter.apply(this);
  }

  default <R, A, S extends Stream<T>> R minus(S stream, Collector<? super T, A, R> collector) {
    return minusBy(stream, identity(), collector);
  }

  default <S extends Stream<T>> Streamed<T> minus(S stream) {
    return minusBy(stream, identity());
  }

  default <R, A, S extends Stream<T>> R minusBy(S stream, Function<? super T, ?> by,
      Collector<? super T, A, R> collector) {
    return minusBy(stream, by).collect(collector);
  }

  default <S extends Stream<T>> Streamed<T> minusBy(S stream, Function<? super T, ?> by) {
    Set<?> keys = stream.map(by).collect(Collectors.toSet());
    return from(stream().filter(e -> !keys.contains(by.apply(e))));
  }

  default Optional<T> last() {
    return reduce((e1, e2) -> e2);
  }

  default Streamed<T> addAt(T value, int index) {
    return zipWithIndex()
        .flatMap(p -> p.isIndexed(index) ? Stream.of(p.value(), value) : Stream.of(p.value()));
  }

  default Streamed<T> append(T... values) {
    return append(Stream.of(values));
  }

  default Streamed<T> append(Collection<? extends T> values) {
    return append(values.stream());
  }

  default Streamed<T> append(Stream<? extends T> values) {
    return () -> Stream.of(this, values).flatMap(identity());
  }

  default Streamed<T> prepend(T... values) {
    return prepend(Stream.of(values));
  }

  default Streamed<T> prepend(Collection<? extends T> values) {
    return prepend(values.stream());
  }

  default Streamed<T> prepend(Stream<? extends T> values) {
    return () -> Stream.of(values, this).flatMap(identity());
  }

  default <S extends Sequence<T>> S toSequence(Function<List<? extends T>, S> factory) {
    return collect(Collect.toSequence(factory));
  }

  default <S extends Bag<T>> S toBag(Function<Set<? extends T>, S> factory) {
    return collect(Collect.toBag(factory));
  }

  default Sequence<T> toSequence() {
    return collect(Collect.toSequence());
  }

  default Bag<T> toBag() {
    return collect(Collect.toBag());
  }

  default Set<T> toSet() {
    return collect(Collectors.toSet());
  }

  default List<T> toList() {
    return collect(Collectors.toList());
  }

  default <C> C collect(Function<? super Iterable<? extends T>, ? extends C> collector) {
    return collector.apply(toList());
  }

  default <K, E> Dictionary<K, E> groupBy(Function<? super T, ? extends K> classifier,
      Function<Streamed<T>, E> mapper) {
    Map<? extends K, E> collect = collect(
        groupingBy(classifier, collectingAndThen(Collectors.toList(), l -> mapper.apply(Streamed.from(l)))));
    return Dictionary.of(collect);
  }

}
