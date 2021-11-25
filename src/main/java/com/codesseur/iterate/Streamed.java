package com.codesseur.iterate;

import static com.codesseur.SafeCaster.safeCastToStream;
import static java.util.Comparator.naturalOrder;
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
import com.codesseur.reflect.Type;
import io.vavr.CheckedFunction1;
import io.vavr.PartialFunction;
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

/**
 * The main class for streaming data. it combines the functionalities of java.util.stream.Stream and more...
 *
 * @param <T>
 * @see java.util.stream.Stream
 */
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

  /**
   * calls the peek consumer if the condition is matched
   *
   * @param condition
   * @param peek
   * @return
   */
  default Streamed<T> peekIf(Predicate<T> condition, Consumer<T> peek) {
    return peek(v -> {
      if (condition.test(v)) {
        peek.accept(v);
      }
    });
  }

  /**
   * @return the first element of the stream if present
   */
  default Optional<T> head() {
    return findFirst();
  }

  /**
   * @return the remaining elements of the stream skipping the first one
   */
  default Streamed<T> tail() {
    return skip(1);
  }

  /**
   * @return split the stream in two head and tail
   */
  default Tuple2<Optional<T>, Streamed<T>> headAndTail() {
    return Tuple.of(head(), tail());
  }

  /**
   * @return split the stream in two lead and last
   */
  default Tuple2<Streamed<T>, Optional<T>> leadAndLast() {
    return Tuple.of(lead(), last());
  }

  /**
   * @return the leading elements of the stream skipping the last one
   */
  default Streamed<T> lead() {
    return zipWithIndex().filter(Indexed::isNotLast).map(Indexed::value);
  }

  /**
   * @return the last element of the stream if present
   */
  default Optional<T> last() {
    return reduce((e1, e2) -> e2);
  }

  /**
   * flatMap then pair with the original element
   *
   * @param mapper
   * @param <E>
   * @return
   */
  default <E> Streamed<Tuple2<T, E>> flatMapSticky(Function<? super T, ? extends Stream<E>> mapper) {
    requireNonNull(mapper);
    return flatMap(e1 -> mapper.apply(e1).map(e -> Tuple.of(e1, e)));
  }

  /**
   * map then pair with the original element
   *
   * @param mapper
   * @param <E>
   * @return
   */
  default <E> Streamed<Tuple2<T, E>> mapSticky(Function<? super T, ? extends E> mapper) {
    requireNonNull(mapper);
    return map(e1 -> Tuple.of(e1, mapper.apply(e1)));
  }

  /**
   * map using a partial function (function defined only for a certain input)
   *
   * @param mapper
   * @param <E>
   * @return
   */
  default <E> Streamed<E> map(PartialFunction<? super T, ? extends E> mapper) {
    return mapPartial(v -> mapper.isDefinedAt(v) ? Optional.ofNullable(mapper.apply(v)) : Optional.empty());
  }

  /**
   * map using a checked function, if an exception is thrown then call the otherwise function
   *
   * @param mapper
   * @param otherwise
   * @param <E>
   * @return
   */
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

  /**
   * map using a checked function, if an exception is thrown then it will be rethrown
   *
   * @param mapper
   * @param <E>
   */
  default <E> Streamed<E> mapTry(CheckedFunction1<? super T, ? extends E> mapper) {
    requireNonNull(mapper);
    return map(Unchecks.Func.uncheck(mapper));
  }

  /**
   * map the last element of the stream
   *
   * @param last
   */
  default Streamed<T> mapLast(Function<? super T, ? extends T> last) {
    requireNonNull(last);
    return mapLastOtherwise(last, identity());
  }

  /**
   * map the last element of the stream using the {@code last} function and the others using the {@code other} function.
   * If the stream contains only one element the {@code last} function will be called
   *
   * @param last
   * @param other
   * @param <E>
   */
  default <E> Streamed<E> mapLastOtherwise(Function<? super T, ? extends E> last,
      Function<? super T, ? extends E> other) {
    requireNonNull(last);
    requireNonNull(other);
    return map(other, other, last, MappingPriority.LAST);
  }

  /**
   * map the first element of the stream
   *
   * @param first
   */
  default Streamed<T> mapFirst(Function<? super T, ? extends T> first) {
    return mapFirstOtherwise(first, identity());
  }

  /**
   * map the first element of the stream using the {@code first} function and the others using the {@code other}
   * function. If the stream contains only one element the {@code first} function will be called
   *
   * @param first
   * @param other
   * @param <E>
   */
  default <E> Streamed<E> mapFirstOtherwise(Function<? super T, ? extends E> first,
      Function<? super T, ? extends E> other) {
    return map(first, other, other, MappingPriority.FIRST);
  }

  /**
   * map the first element of the stream using the {@code first} function,the last element of the stream using the
   * {@code last} function and the others using the {@code middle} function. If the stream contains only one element the
   * {@code mappingPriority} will determine whether to call the {@code first} function or the {@code last} function
   *
   * @param first
   * @param middle
   * @param last
   * @param <E>
   */
  default <E> Streamed<E> map(Function<? super T, ? extends E> first,
      Function<? super T, ? extends E> middle,
      Function<? super T, ? extends E> last,
      MappingPriority mappingPriority) {
    return zipWithIndex().map(i -> mappingPriority.handle(i, first, middle, last));
  }

  /**
   * flatMap from an iterable
   *
   * @param mapper
   * @param <E>
   */
  default <E> Streamed<E> flatMapIterable(Function<? super T, ? extends Iterable<E>> mapper) {
    return map(mapper).flatMap(v -> StreamSupport.stream(v.spliterator(), false));
  }

  /**
   * try to cast the elements to {@code type} ignoring those which doesn't
   *
   * @param type
   * @param <E>
   */
  default <E> Streamed<E> safeCast(Class<E> type) {
    return flatMap(v -> safeCastToStream(v, type));
  }

  /**
   * try to cast the elements to {@code type} ignoring those which doesn't
   *
   * @param type
   * @param <E>
   */
  default <E> Streamed<E> safeCast(Type<E> type) {
    return flatMap(v -> safeCastToStream(v, type));
  }

  /**
   * map elements ignoring empty results
   *
   * @param mapper
   * @param <E>
   */
  default <E> Streamed<E> mapPartial(Function<? super T, ? extends Optional<E>> mapper) {
    return map(mapper).flatMap(Optionals::stream);
  }

  /**
   * replace element if condition matched
   *
   * @param condition
   * @param value
   */
  default Streamed<T> replaceIf(Predicate<T> condition, T value) {
    return replaceIf(condition, i -> value);
  }

  /**
   * replace element using the {@code mapper} function if condition matched
   *
   * @param condition
   * @param mapper
   */
  default Streamed<T> replaceIf(Predicate<T> condition, Function<? super T, ? extends T> mapper) {
    return replaceIfOr(condition, mapper, () -> this);
  }

  /**
   * replace element using the {@code mapper} function if {@code condition} matched or else append a new element from
   * {@code supplier}
   *
   * @param condition
   * @param mapper
   * @param append
   */
  default Streamed<T> replaceIfOrAppend(Predicate<T> condition, Function<? super T, ? extends T> mapper,
      Supplier<? extends T> append) {
    return replaceIfOr(condition, mapper, () -> append(append.get()));
  }

  /**
   * replace element using the {@code mapper} function if {@code condition} matched or else append a stream from {@code
   * otherwise}
   *
   * @param condition
   * @param mapper
   * @param otherwise
   * @return
   */
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

  /**
   * remove element if condition matched
   *
   * @param condition
   */
  default Streamed<T> removeIf(Predicate<T> condition) {
    return filter(condition.negate());
  }

  /**
   * remove element at a certain index
   *
   * @param index
   */
  default Streamed<T> removeAt(long index) {
    return zipWithIndex().filter(p -> p.isNotAt(index)).map(Indexed::value);
  }

  /**
   * truncate stream from offset to offset+length exclusive
   *
   * @param offset
   * @param length
   */
  default Streamed<T> take(long offset, long length) {
    return skip(offset).limit(length);
  }

  /**
   * keep only distinct elements using keyExtractor resolving duplicates if exists with duplicateResolver
   *
   * @param keyExtractor
   * @param duplicateResolver
   */
  default Streamed<T> distinctBy(Function<? super T, ?> keyExtractor, BinaryOperator<T> duplicateResolver) {
    return of(collect(groupingBy(keyExtractor, reducing(duplicateResolver))).values()).flatMap(Optionals::stream);
  }

  /**
   * keep only distinct elements using keyExtractor
   *
   * @param keyExtractor
   */
  default Streamed<T> distinctBy(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return filter(t -> seen.add(keyExtractor.apply(t)));
  }

  /**
   * zip elements with the iterable keeping only elements from both ends
   *
   * @param second
   * @param <E>
   * @param <I>
   * @return
   */
  default <E, I extends Iterable<E>> Streamed<Tuple2<T, E>> innerZip(I second) {
    return zip(second, ZipMode.INTERSECT)
        .map(Indexed::value)
        .map(p -> p.apply(Optionals::and))
        .flatMap(Optionals::stream);
  }

  /**
   * zip elements with the iterable keeping all elements
   *
   * @param second
   * @param <E>
   * @param <I>
   */
  default <E, I extends Iterable<E>> Streamed<Tuple2<Optional<T>, Optional<E>>> outerZip(I second) {
    return zip(second, ZipMode.UNION).map(Indexed::value);
  }

  /**
   * zip elements with the iterable keeping all elements of the left end
   *
   * @param second
   * @param <E>
   * @param <I>
   */
  default <E, I extends Iterable<E>> Streamed<Tuple2<T, Optional<E>>> leftOuterZip(I second) {
    return zip(second, ZipMode.UNION).map(Indexed::value).mapPartial(t -> t._1().map(v -> t.map1(i -> v)));
  }

  /**
   * zip elements with the iterable keeping all elements of the right end
   *
   * @param second
   * @param <E>
   * @param <I>
   */
  default <E, I extends Iterable<E>> Streamed<Tuple2<Optional<T>, E>> rightOuterZip(I second) {
    return zip(second, ZipMode.UNION).map(Indexed::value).mapPartial(t -> t._2().map(v -> t.map2(i -> v)));
  }

  /**
   * zip elements with their respective index
   */
  default Streamed<Indexed<T>> zipWithIndex() {
    return zip(Collections.emptyList(), ZipMode.UNION)
        .map(i -> i.map(Tuple2::_1).map(o -> o.orElse(null)));
  }

  /**
   * zip elements with the iterable using zipMode whether to keeping all (UNION) or only(INTERSECT) elements of the both
   * ends
   *
   * @param second
   * @param zipMode
   * @param <E>
   * @param <I>
   */
  default <E, I extends Iterable<E>> Streamed<Indexed<Tuple2<Optional<T>, Optional<E>>>> zip(I second,
      ZipMode zipMode) {
    Iterable<Indexed<Tuple2<Optional<T>, Optional<E>>>> iterable = () -> new ZippedIterator<>(stream().iterator(),
        second.iterator(), zipMode);
    return of(StreamSupport.stream(iterable.spliterator(), false));
  }

  /**
   * check if stream contains element
   *
   * @param element
   */
  default boolean contains(T element) {
    return contains(e -> e.equals(element));
  }

  /**
   * check if stream has a matching condition
   *
   * @param condition
   */
  default boolean contains(Predicate<? super T> condition) {
    return anyMatch(condition);
  }

  /**
   * find the first element giving a condition
   *
   * @param condition
   */
  default Optional<T> find(Predicate<? super T> condition) {
    return filter(condition).findFirst();
  }

  default Optional<T> findLeftClosest(ToLongFunction<? super T> distance) {
    return findClosest(distance, t -> t <= 0);
  }

  default Optional<T> findRightClosest(ToLongFunction<? super T> distance) {
    return findClosest(distance, t -> t >= 0);
  }

  default Optional<T> findClosest(ToLongFunction<? super T> distance) {
    return findClosest(distance, t -> true);
  }

  default Optional<T> findClosest(ToLongFunction<? super T> distance, Predicate<? super Long> distanceFilter) {
    return map(v -> Tuple.of(distance.applyAsLong(v), v))
        .filter(t -> distanceFilter.test(t._1))
        .map(t -> t.map1(Math::abs))
        .min(naturalOrder())
        .map(Tuple2::_2);
  }

  default <E> E foldLeft(Supplier<E> seed, BiFunction<E, ? super T, E> accumulator) {
    return reduce(seed.get(), accumulator, (v1, v2) -> v1);
  }

  default Optional<T> foldLeft(BinaryOperator<T> accumulator) {
    return reduce(accumulator);
  }

  default <E> Optional<E> foldLeft(Function<? super T, ? extends E> seed, BiFunction<E, ? super T, E> accumulator) {
    return headAndTail().apply((head, tail) -> head.map(v -> tail.foldLeft(() -> seed.apply(v), accumulator)));
  }

  default <E> E foldRight(Supplier<E> seed, BiFunction<? super T, E, E> accumulator) {
    E result = seed.get();
    final List<T> list = toList();
    for (int i = list.size() - 1; i >= 0; i--) {
      result = accumulator.apply(list.get(i), result);
    }
    return result;
  }

  default Optional<T> foldRight(BinaryOperator<T> accumulator) {
    return foldRight(identity(), accumulator);
  }

  default <E> Optional<E> foldRight(Function<? super T, ? extends E> seed, BiFunction<? super T, E, E> accumulator) {
    return leadAndLast().apply((lead, last) -> last.map(v -> lead.foldRight(() -> seed.apply(v), accumulator)));
  }

  default <TT, I extends Iterable<TT>> Joiner<T, TT> join(I iterable) {
    return new Joiner<>(this, Streamed.of(iterable));
  }

  default <TT> Streamed<T> project(Set<TT> keys, Function<? super T, ? extends TT> extractor) {
    return filter(e -> keys.contains(extractor.apply(e)));
  }

  default Streamed<T> remove(T value) {
    return remove(value);
  }

  default Streamed<T> remove(T... value) {
    return removeBy(List.of(value), identity());
  }

  default <I extends Iterable<T>> Streamed<T> remove(I iterable) {
    return removeBy(iterable, identity());
  }

  default <I extends Iterable<T>> Streamed<T> removeBy(I iterable, Function<? super T, ?> by) {
    Set<?> keys = Streamed.of(iterable).map(by).toSet();
    return of(stream().filter(e -> !keys.contains(by.apply(e))));
  }

  default Streamed<T> addAt(T value, long index) {
    return zipWithIndex().flatMap(p -> p.isAt(index) ? Stream.of(p.value(), value) : Stream.of(p.value()));
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

  default <S> Optional<S> ifNotEmpty(Function<? super Streamed<T>, ? extends S> factory) {
    Iterator<T> iterator = iterator();
    return iterator.hasNext() ? Optional.of(Streamed.of(iterator)).map(factory) : Optional.empty();
  }

  default <S> S toListThen(Function<? super List<T>, ? extends S> factory) {
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
