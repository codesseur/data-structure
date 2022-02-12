package com.codesseur.iterate;

import static com.codesseur.SafeCaster.safeCastToStream;
import static com.codesseur.iterate.SplitMode.RIGHT;
import static java.util.Comparator.comparing;
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
import com.codesseur.reflect.Type.$;
import io.vavr.CheckedFunction1;
import io.vavr.PartialFunction;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

  /**
   * create a Streamed from Optionals array ignoring empty elements
   *
   * @param values
   * @param <T>
   * @return
   */
  @SafeVarargs
  static <T> Streamed<T> nonEmptyOf(Optional<T>... values) {
    return values == null ? Streamed.empty() : of(Stream.of(values).flatMap(Optionals::stream));
  }

  /**
   * create a Streamed from nullables array ignoring null elements
   *
   * @param values
   * @param <T>
   * @return
   */
  @SafeVarargs
  static <T> Streamed<T> nonNullOf(T... values) {
    return values == null ? Streamed.empty() : of(Stream.of(values).filter(Objects::nonNull));
  }

  /**
   * create a Streamed from nullables iterable ignoring null elements
   *
   * @param values
   * @param <T>
   * @return
   */
  static <T> Streamed<T> nonNullOf(Iterable<? extends T> values) {
    return values == null ? Streamed.empty() : Streamed.of(values).nonNull().safeCast($.$());
  }

  /**
   * create a Streamed from nullable items
   *
   * @param values
   * @param <T>
   * @return
   */
  @SafeVarargs
  static <T> Streamed<T> of(T... values) {
    return values == null ? Streamed.empty() : of(Stream.of(values));
  }

  /**
   * create a Streamed from an iterator
   *
   * @param values
   * @param <T>
   * @return
   */
  static <T> Streamed<T> of(Iterator<? extends T> values) {
    return values == null ? Streamed.empty()
        : () -> StreamSupport.stream(spliteratorUnknownSize(values, ORDERED), false);
  }

  static <T> Streamed<T> of(Iterable<? extends T> values) {
    return values == null ? Streamed.empty() : of(StreamSupport.stream(values.spliterator(), false));
  }

  static <T> Streamed<T> of(Stream<? extends T> values) {
    return Optional.ofNullable(values)
        .<Streamed<T>>map(v -> {
          Stream<T> ts = Stream.of(values).flatMap(identity());
          return () -> ts;
        })
        .orElseGet(Streamed::empty);
  }

  @SafeVarargs
  static <T> Streamed<T> of(Stream<? extends T>... values) {
    Stream<T> ts = Optional.ofNullable(values).map(s -> Stream.of(s).flatMap(identity())).orElseGet(Stream::empty);
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
   * calls the peek consumer if the condition is matched <br> Support: Infinite Stream, Parallel Stream, Finit Stream,
   * Sequential Stream
   *
   * @param condition: a Predicate to apply to each element to determine if it should be peeked
   * @param peek:      a Consumer to call with the element
   * @return the new Streamed
   */
  default Streamed<T> peekIf(Predicate<T> condition, Consumer<T> peek) {
    return peek(v -> {
      if (condition.test(v)) {
        peek.accept(v);
      }
    });
  }

  /**
   * return the first element of the stream if present <br> Support: Infinite Stream, Parallel Stream, Finit Stream,
   * Sequential Stream
   *
   * @return the head of the Streamed
   */
  default Optional<T> head() {
    return findFirst();
  }

  /**
   * return the remaining elements of the stream skipping the first one <br> Support: Infinite Stream, Parallel Stream,
   * Finit Stream, Sequential Stream
   *
   * @return the tail
   */
  default Streamed<T> tail() {
    return skip(1);
  }

  /**
   * return head and tail see {@link #head()} and {@link #tail()}
   *
   * @return split the stream in head and tail
   */
  default Tuple2<Optional<T>, Streamed<T>> headAndTail() {
    Iterator<T> iterator = iterator();
    return iterator.hasNext() ?
        Tuple.of(Optional.of(iterator.next()), Streamed.of(iterator)) : Tuple.of(Optional.empty(), Streamed.empty());
  }

  /**
   * return lead and last see {@link #lead()} ()} and {@link #last()}
   *
   * @return split the stream in lead and last
   */
  default Tuple2<Streamed<T>, Optional<T>> lastAndLead() {
    Sequence<T> sequence = toSequence();
    return Tuple.of(sequence.lead(), sequence.last());
  }

  /**
   * return the leading elements of the stream skipping the last one
   *
   * @return the lead
   */
  default Streamed<T> lead() {
    return zipWithIndex().filter(Indexed::isNotLast).map(Indexed::value);
  }

  /**
   * return the last element of the stream if present
   *
   * @return the last
   */
  default Optional<T> last() {
    return reduce((e1, e2) -> e2);
  }

  /**
   * flatMap then pair with the original element <br> Support: Infinite Stream, Parallel Stream, Finit Stream,
   * Sequential Stream
   *
   * @param mapper: mapper to use
   * @param <E>:    mapping type
   * @return the new Streamed
   */
  default <E> Streamed<Tuple2<T, E>> flatMapSticky(Function<? super T, ? extends Stream<E>> mapper) {
    requireNonNull(mapper);
    return flatMap(e1 -> mapper.apply(e1).map(e -> Tuple.of(e1, e)));
  }

  /**
   * map then pair with the original element <br> Support: Infinite Stream, Parallel Stream, Finit Stream, Sequential
   * Stream
   *
   * @param mapper: mapper to use
   * @param <E>:    mapping type
   * @return the new Streamed
   */
  default <E> Streamed<Tuple2<T, E>> mapSticky(Function<? super T, ? extends E> mapper) {
    requireNonNull(mapper);
    return map(e1 -> Tuple.of(e1, mapper.apply(e1)));
  }

  /**
   * map using a partial function (function defined only for a certain input) <br> Support: Infinite Stream, Parallel
   * Stream, Finit Stream, Sequential Stream
   *
   * @param mapper: partial function
   * @param <E>:    mapping type
   * @return the new Streamed
   */
  default <E> Streamed<E> map(PartialFunction<? super T, ? extends E> mapper) {
    return mapPartial(v -> mapper.isDefinedAt(v) ? Optional.ofNullable(mapper.apply(v)) : Optional.empty());
  }

  /**
   * map using a checked function, if an exception is thrown then call the otherwise function <br> Support: Infinite
   * Stream, Parallel Stream, Finit Stream, Sequential Stream
   *
   * @param mapper:    throwing function
   * @param otherwise: function used if exception is thrown
   * @param <E>:       mapping type
   * @return the new Streamed
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
   * map using a checked function, if an exception is thrown then it will be rethrown <br> Support: Infinite Stream,
   * Parallel Stream, Finit Stream, Sequential Stream
   *
   * @param mapper: throwing function
   * @param <E>:    mapping type
   * @return the new Streamed
   */
  default <E> Streamed<E> mapTry(CheckedFunction1<? super T, ? extends E> mapper) {
    requireNonNull(mapper);
    return map(Unchecks.Func.uncheck(mapper));
  }

  /**
   * map the last element of the stream
   *
   * @param last: mapping function
   * @return the new Streamed
   */
  default Streamed<T> mapLast(Function<? super T, ? extends T> last) {
    requireNonNull(last);
    return mapLastOtherwise(last, identity());
  }

  /**
   * map the last element of the stream using the {@code last} function and the others using the {@code other} function.
   * If the stream contains only one element the {@code last} function will be called
   *
   * @param last:  mapping function for the last element
   * @param other: mapping function for the other
   * @param <E>:   mapping type
   * @return the new Streamed
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
   * @param first: mapping function for the first element
   * @return the new Streamed
   */
  default Streamed<T> mapFirst(Function<? super T, ? extends T> first) {
    return mapFirstOtherwise(first, identity());
  }

  /**
   * map the first element of the stream using the {@code first} function and the others using the {@code other}
   * function. If the stream contains only one element the {@code first} function will be called
   *
   * @param first: mapping function for the first element
   * @param other: mapping function for the other
   * @param <E>:   mapping type
   * @return the new Streamed
   */
  default <E> Streamed<E> mapFirstOtherwise(Function<? super T, ? extends E> first,
      Function<? super T, ? extends E> other) {
    return map(first, other, other, MappingPriority.FIRST);
  }

  /**
   * map the first element of the stream using the {@code first} function,the last element of the stream using the
   * {@code last} function and the others using the {@code middle} function. If the stream contains only one element the
   * {@code mappingPriority} will determine whether to call the {@code first} function or the {@code last} function
   * <p>
   * <br> Support: Infinite Stream, Finit Stream, Sequential Stream
   *
   * @param first:  mapping function for the first element
   * @param middle: mapping function for the other
   * @param last:   mapping function for the last element
   * @param <E>:    mapping type
   * @return the new Streamed
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
   * @param mapper: mapper function
   * @param <E>:    mapping type
   * @return the new Streamed
   */
  default <E> Streamed<E> flatMapIterable(Function<? super T, ? extends Iterable<E>> mapper) {
    return map(mapper).flatMap(v -> StreamSupport.stream(v.spliterator(), false));
  }

  /**
   * try to cast the elements to {@code type} ignoring those which doesn't
   *
   * @param type: class to cast
   * @param <E>:  mapping type
   * @return the new Streamed
   */
  default <E> Streamed<E> safeCast(Class<E> type) {
    return flatMap(v -> safeCastToStream(v, type));
  }

  /**
   * try to cast the elements to {@code type} ignoring those which doesn't
   *
   * @param type: type to cast
   * @param <E>:  mapping type
   * @return the new Streamed
   */
  default <E> Streamed<E> safeCast(Type<E> type) {
    return flatMap(v -> safeCastToStream(v, type));
  }

  /**
   * map elements ignoring empty results
   *
   * @param mapper: mapper function
   * @param <E>:    mapping type
   * @return the new Streamed
   */
  default <E> Streamed<E> mapPartial(Function<? super T, ? extends Optional<E>> mapper) {
    return map(mapper).flatMap(Optionals::stream);
  }

  default Streamed<T> nonNull() {
    return filter(Objects::nonNull);
  }

  /**
   * replace element using the {@code mapper} function if condition matched
   *
   * @param condition: predicate to match
   * @param replacer:    mapping function
   * @return the new Streamed
   */
  default Streamed<T> replaceIf(Predicate<T> condition, Function<? super T, ? extends T> replacer) {
    return map(v -> condition.test(v) ? replacer.apply(v) : v);
  }

  /**
   * remove element if condition matched
   *
   * @param condition: predicate to match
   * @return the new Streamed
   */
  default Streamed<T> removeIf(Predicate<T> condition) {
    return filter(condition.negate());
  }

  /**
   * remove element at a certain index
   *
   * @param index: position of the element zero based
   * @return the new Streamed
   */
  default Streamed<T> removeAt(long index) {
    return zipWithIndex().filter(p -> p.isNotAt(index)).map(Indexed::value);
  }

  /**
   * truncate stream from offset to offset+length exclusive
   *
   * @param offset: starting position inclusive
   * @param length: inclusion size
   * @return the new Streamed
   */
  default Streamed<T> take(long offset, long length) {
    return skip(offset).limit(length);
  }

  /**
   * keep only distinct elements using keyExtractor resolving duplicates if exists with duplicateResolver
   *
   * @param keyExtractor:      function to get the distinction key
   * @param duplicateResolver: function to resolve duplicates
   * @return the new Streamed
   */
  default Streamed<T> distinctBy(Function<? super T, ?> keyExtractor, BinaryOperator<T> duplicateResolver) {
    return of(collect(groupingBy(keyExtractor, reducing(duplicateResolver))).values()).flatMap(Optionals::stream);
  }

  /**
   * keep only distinct elements using keyExtractor
   *
   * @param keyExtractor: function to get the distinction key
   * @return the new Streamed
   */
  default Streamed<T> distinctBy(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return filter(t -> seen.add(keyExtractor.apply(t)));
  }

  /**
   * zip elements with the iterable keeping only elements from both ends
   *
   * @param second: iterable
   * @param <E>
   * @param <I>
   * @return the new Streamed
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
   * @return the new Streamed
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
   * @return the new Streamed
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
   * @return the new Streamed
   */
  default <E, I extends Iterable<E>> Streamed<Tuple2<Optional<T>, E>> rightOuterZip(I second) {
    return zip(second, ZipMode.UNION).map(Indexed::value).mapPartial(t -> t._2().map(v -> t.map2(i -> v)));
  }

  /**
   * zip elements with their respective index <br> Support: Infinite Stream, Finit Stream, Sequential Stream
   *
   * @return the new Streamed
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
   * @return the new Streamed
   */
  default <E, I extends Iterable<E>> Streamed<Indexed<Tuple2<Optional<T>, Optional<E>>>> zip(I second,
      ZipMode zipMode) {
    Iterable<Indexed<Tuple2<Optional<T>, Optional<E>>>> iterable = () -> new ZippedIterator<>(stream().iterator(),
        second.iterator(), zipMode);
    return of(StreamSupport.stream(iterable.spliterator(), false));
  }

  /**
   * ignore elements until Support: Infinite Stream, Parallel Stream, Finit Stream, Sequential Stream
   *
   * @return the new Streamed
   */
  default Streamed<T> ignoreUntil(Predicate<T> start) {
    return Streamed.of(foldLeft((Supplier<ArrayList<T>>) ArrayList::new, (list, v) -> {
      if (start.test(v)) {
        list.clear();
      }
      list.add(v);
      return list;
    }));
  }

  /**
   * split stream by size then apply "then" function
   *
   * @return the new Streamed
   */
  default <E> Streamed<E> splitThen(int size, Function<Streamed<T>, E> then) {
    return split(size).map(then);
  }

  /**
   * split stream by size
   *
   * @param size: number of elements on each chunk
   * @return the new Streamed
   */

  default Streamed<Streamed<T>> split(int size) {
    return zipWithIndex().split(v -> v.isNotFirst() && v.indexMultipleOf(size), RIGHT).map(s -> s.map(Indexed::value));
  }

  /**
   * split stream based on a predicate and a split mode
   *
   * @return the new Streamed
   */
  default Streamed<Streamed<T>> split(Predicate<T> start, SplitMode splitMode) {
    Function<T, Optional<Either<T, T>>> junction;
    switch (splitMode) {
      case LEFT:
        junction = v -> Optional.of(Either.left(v));
        break;
      case RIGHT:
        junction = v -> Optional.of(Either.right(v));
        break;
      default:
        junction = v -> Optional.empty();
    }
    return Streamed.of(new SplitIterator<>(iterator(), start, junction)).map(Streamed::of);
  }

  /**
   * check if stream contains element
   *
   * @param element: element to match
   * @return the result
   */
  default boolean contains(T element) {
    return contains(e -> e.equals(element));
  }

  /**
   * check if stream has a matching condition
   *
   * @param condition: predicate to match
   * @return the result
   */
  default boolean contains(Predicate<? super T> condition) {
    return anyMatch(condition);
  }

  /**
   * check if stream has an element of type
   *
   * @param type: type to match
   * @return the result
   */
  default boolean contains(Class<? super T> type) {
    return anyMatch(type::isInstance);
  }

  /**
   * find the first element giving a condition
   *
   * @param condition: predicate to match
   * @return the result
   */
  default Optional<T> find(Predicate<? super T> condition) {
    return filter(condition).findFirst();
  }

  /**
   * @param distance: distance function
   * @return
   */
  default Optional<T> findLeftClosest(ToLongFunction<? super T> distance) {
    return findClosest(distance, t -> t <= 0);
  }

  /**
   * @param distance
   * @return
   */
  default Optional<T> findRightClosest(ToLongFunction<? super T> distance) {
    return findClosest(distance, t -> t >= 0);
  }

  /**
   * @param distance
   * @return
   */
  default Optional<T> findClosest(ToLongFunction<? super T> distance) {
    return findClosest(distance, t -> true);
  }

  /**
   * @param distance
   * @param distanceFilter
   * @return
   */
  default Optional<T> findClosest(ToLongFunction<? super T> distance, Predicate<? super Long> distanceFilter) {
    return map(v -> Tuple.of(distance.applyAsLong(v), v))
        .filter(t -> distanceFilter.test(t._1))
        .map(t -> t.map1(Math::abs))
        .min(comparing(Tuple2::_1))
        .map(Tuple2::_2);
  }

  /**
   * @param seed
   * @param accumulator
   * @param <E>
   * @return
   */
  default <E> E foldLeft(Supplier<E> seed, BiFunction<E, ? super T, E> accumulator) {
    return reduce(seed.get(), accumulator, (v1, v2) -> v1);
  }

  /**
   * @param accumulator
   * @return
   */
  default Optional<T> foldLeft(BinaryOperator<T> accumulator) {
    return reduce(accumulator);
  }

  /**
   * @param seed
   * @param accumulator
   * @param <E>
   * @return
   */
  default <E> Optional<E> foldLeft(Function<? super T, ? extends E> seed, BiFunction<E, ? super T, E> accumulator) {
    return headAndTail().apply((head, tail) -> head.map(v -> tail.foldLeft(() -> seed.apply(v), accumulator)));
  }

  /**
   * @param seed
   * @param accumulator
   * @param <E>
   * @return
   */
  default <E> E foldRight(Supplier<E> seed, BiFunction<? super T, E, E> accumulator) {
    E result = seed.get();
    final List<T> list = toList();
    for (int i = list.size() - 1; i >= 0; i--) {
      result = accumulator.apply(list.get(i), result);
    }
    return result;
  }

  /**
   * @param accumulator
   * @return
   */
  default Optional<T> foldRight(BinaryOperator<T> accumulator) {
    return foldRight(identity(), accumulator);
  }

  /**
   * the
   *
   * @param seed
   * @param accumulator
   * @param <E>
   * @return
   */
  default <E> Optional<E> foldRight(Function<? super T, ? extends E> seed, BiFunction<? super T, E, E> accumulator) {
    return lastAndLead().apply((lead, last) -> last.map(v -> lead.foldRight(() -> seed.apply(v), accumulator)));
  }

  /**
   * join this Streamed with an iterable
   *
   * @param iterable: iterable to join with
   * @param <TT>:     iterable element type
   * @param <I>:      iterable type
   * @return the joiner to further configure the joins
   */
  default <TT, I extends Iterable<TT>> Joiner<T, TT> join(I iterable) {
    return new Joiner<>(this, Streamed.of(iterable));
  }

  /**
   * remove an element from the Streamed
   *
   * @param value: the element
   * @return the new Streamed
   */
  default Streamed<T> remove(T value) {
    return removeBy(List.of(value), identity());
  }

  /**
   * remove an array elements from the Streamed
   *
   * @param value: array of elements
   * @return the new Streamed
   */
  default Streamed<T> remove(T... value) {
    return removeBy(List.of(value), identity());
  }

  /**
   * remove elements from the Streamed
   *
   * @param iterable: elements to remove
   * @param <I>:      the Iterable type
   * @return the new Streamed
   */
  default <I extends Iterable<T>> Streamed<T> remove(I iterable) {
    return removeBy(iterable, identity());
  }

  /**
   * remove elements from the Streamed using the by function
   *
   * @param iterable: elements to remove
   * @param by:       called on each element of the Streamed and the iterable to determine if the element must be
   *                  removed
   * @param <I>:      the Iterable type
   * @return the new Streamed
   */
  default <I extends Iterable<T>> Streamed<T> removeBy(I iterable, Function<? super T, ?> by) {
    Set<?> keys = Streamed.of(iterable).map(by).toSet();
    return of(stream().filter(e -> !keys.contains(by.apply(e))));
  }

  /**
   * add an element at a certain index, if index is negative or greater than the length of the stream then nothing will
   * happen <br> Support: Infinite Stream, Finit Stream,Sequential Stream
   *
   * @param value: the elements  to add
   * @param index: position on the Streamed
   * @return th new Streamed
   */
  default Streamed<T> addAt(T value, long index) {
    return index < 0 ? this
        : zipWithIndex().flatMap(p -> p.isAt(index) ? Stream.of(p.value(), value) : Stream.of(p.value()));
  }

  /**
   * add an element to the end of the Streamed
   *
   * @param value: the element
   * @return the new Streamed
   */
  default Streamed<T> append(T value) {
    return append(List.of(value));
  }

  /**
   * add an array of elements to the end of the Streamed
   *
   * @param values: array of element
   * @return the new Streamed
   */
  default Streamed<T> append(T... values) {
    return append(Stream.of(values));
  }

  /**
   * add an Iterable to the end of the Streamed
   *
   * @param values: the Iterable
   * @param <I>:    the Iterable type
   * @return the new Streamed
   */
  default <I extends Iterable<T>> Streamed<T> append(I values) {
    return append((Stream<T>) of(values));
  }

  /**
   * add an array of Stream to the end of the Streamed
   *
   * @param values: array of Stream
   * @return the new Streamed
   */
  default Streamed<T> append(Stream<T>... values) {
    return Streamed.of(Stream.of(stream()), Stream.of(values)).flatMap(identity());
  }

  /**
   * add an element to the beginning of the Streamed
   *
   * @param value: the element
   * @return the new Streamed
   */
  default Streamed<T> prepend(T value) {
    return prepend(List.of(value));
  }

  /**
   * add an array of elements to the beginning of the Streamed
   *
   * @param values: array of element
   * @return the new Streamed
   */
  default Streamed<T> prepend(T... values) {
    return prepend(Stream.of(values));
  }

  /**
   * add an Iterable to the beginning of the Streamed
   *
   * @param values: the Iterable
   * @param <I>:    the Iterable type
   * @return the new Streamed
   */
  default <I extends Iterable<T>> Streamed<T> prepend(I values) {
    return prepend((Stream<T>) Streamed.of(values));
  }

  /**
   * add an array of Stream to the beginning of the Streamed
   *
   * @param values: array of Stream
   * @return the new Streamed
   */
  default Streamed<T> prepend(Stream<T>... values) {
    return Streamed.of(Stream.of(values), Stream.of(stream())).flatMap(identity());
  }

  /**
   * wraps every element with an Optional
   *
   * @return the new Streamed
   */
  default Streamed<Optional<T>> asOptionals() {
    return map(Optional::ofNullable);
  }

  /**
   * maps the Streamed if not empty
   *
   * @param then: called when the Streamed is empty
   * @param <V>:  type of the result
   * @return the collected result
   */
  default <V> Optional<V> toOptional(Function<Streamed<T>, V> then) {
    Iterator<T> iterator = iterator();
    return iterator.hasNext() ? Optional.of(Streamed.of(iterator)).map(then) : Optional.empty();
  }

  /**
   * collect elements into a Sequence then maps the result
   *
   * @param then: further maps the Sequence
   * @param <S>:  type of the result
   * @return the collected result
   */
  default <S extends Sequence<T>> S toSequence(Function<? super List<T>, ? extends S> then) {
    return then.apply(toList());
  }

  /**
   * collect elements into a Bag then maps the result
   *
   * @param then: further maps the Bag
   * @param <S>:  type of the result
   * @return the collected result
   */
  default <S extends Bag<T>> S toBag(Function<? super Set<T>, ? extends S> then) {
    return then.apply(toSet());
  }

  /**
   * collect elements into a Sequence
   *
   * @return the collected Sequence
   */
  default Sequence<T> toSequence() {
    return this instanceof Sequence ? (Sequence<T>) this : collect(Collect.toSequence());
  }

  /**
   * collect elements into a Bag
   *
   * @return the collected Bag
   */
  default Bag<T> toBag() {
    return this instanceof Bag ? (Bag<T>) this : collect(Collect.toBag());
  }

  /**
   * collect elements into a Set then maps the result
   *
   * @param then: further maps the Set
   * @param <S>:  type of the result
   * @return the collected result
   */
  default <S> S toSetThen(Function<? super Set<T>, ? extends S> then) {
    return then.apply(toSet());
  }

  /**
   * collect elements into a Set
   *
   * @return the collected Set
   */
  default Set<T> toSet() {
    return this instanceof Bag ? ((Bag<T>) this).value() : collect(Collectors.toSet());
  }

  /**
   * maps the Streamed if it's not empty
   *
   * @param factory: further maps the Streamed
   * @param <S>:     type of the result
   * @return the collected result
   */
  default <S> Optional<S> ifNotEmpty(Function<? super Streamed<T>, ? extends S> factory) {
    Iterator<T> iterator = iterator();
    return iterator.hasNext() ? Optional.of(Streamed.of(iterator)).map(factory) : Optional.empty();
  }

  /**
   * collect elements into a List then maps the result
   *
   * @param then: further maps the List
   * @param <S>:  type of the result
   * @return the collected result
   */
  default <S> S toListThen(Function<? super List<T>, ? extends S> then) {
    return then.apply(toList());
  }

  /**
   * collect elements into a List
   *
   * @return the collected List
   */
  default List<T> toList() {
    return this instanceof Sequence ? ((Sequence<T>) this).value() : collect(Collectors.toList());
  }

  /**
   * collect elements into a Map using keyExtractor and elements as values. Duplicates are overridden
   *
   * @param keyExtractor called to get the key
   * @param <K>:         key type
   * @return the collected Map
   */
  default <K> Map<K, T> toMap(Function<? super T, ? extends K> keyExtractor) {
    return toMap(keyExtractor, Function.identity());
  }

  /**
   * Collect elements into a Map using elements as keys en valueExtractor. Duplicates are overridden
   *
   * @param valueExtractor: called to get the value
   * @param <V>:            value type
   * @return the collected Map
   */
  default <V> Map<T, V> toMap2(Function<? super T, ? extends V> valueExtractor) {
    return toMap(Function.identity(), valueExtractor);
  }

  /**
   * Collect elements into a Map using keyExtractor and valueExtractor on each element. Duplicates are overridden
   *
   * @param keyExtractor:   called to get the key
   * @param valueExtractor: called to get the value
   * @param <K>:            key type
   * @param <V>:            value type
   * @return the collected Map
   */
  default <K, V> Map<K, V> toMap(Function<? super T, ? extends K> keyExtractor,
      Function<? super T, ? extends V> valueExtractor) {
    return collect(Collect.toMap(keyExtractor, valueExtractor));
  }

  /**
   * collect elements into a Dictionary using keyExtractor and elements as values. Duplicates are overridden
   *
   * @param keyExtractor called to get the key
   * @param <K>:         key type
   * @return the collected Dictionary
   */
  default <K> Dictionary<K, T> toDictionary(Function<? super T, ? extends K> keyExtractor) {
    return Dictionary.of(toMap(keyExtractor));
  }

  /**
   * Collect elements into a Dictionary using elements as keys en valueExtractor. Duplicates are overridden
   *
   * @param valueExtractor: called to get the value
   * @param <V>:            value type
   * @return the collected Dictionary
   */
  default <V> Dictionary<T, V> toDictionary2(Function<? super T, ? extends V> valueExtractor) {
    return toDictionary(Function.identity(), valueExtractor);
  }

  /**
   * Collect elements into a Dictionary using keyExtractor and valueExtractor on each element. Duplicates are
   * overridden
   *
   * @param keyExtractor:   called to get the key
   * @param valueExtractor: called to get the value
   * @param <K>:            key type
   * @param <V>:            value type
   * @return the collected Dictionary
   */
  default <K, V> Dictionary<K, V> toDictionary(Function<? super T, ? extends K> keyExtractor,
      Function<? super T, ? extends V> valueExtractor) {
    return Dictionary.of(toMap(keyExtractor, valueExtractor));
  }

  /**
   * Collect elements using the collector
   *
   * @param collector: the used collector
   * @param <C>:       the result type
   * @return the collected result
   */
  default <C> C collect(Function<? super Iterable<? extends T>, ? extends C> collector) {
    return collector.apply(toList());
  }

  /**
   * groups the elements by the classifier
   *
   * @param classifier: called for every element to get the group key
   * @param <K>:        type of the group key
   * @return the resulting Dictionary
   */
  default <K> Dictionary<K, Streamed<T>> groupBy(Function<? super T, ? extends K> classifier) {
    return groupBy(classifier, identity());
  }

  /**
   * groups the elements by the classifier then maps the resulting groups
   *
   * @param classifier: called for every element to get the group key
   * @param then:       further maps the group
   * @param <K>:        type of the group key
   * @param <E>:        type of the resulting elements
   * @return the resulting Dictionary
   */
  default <K, E> Dictionary<K, E> groupBy(Function<? super T, ? extends K> classifier,
      Function<Streamed<T>, E> then) {
    Map<? extends K, E> collect = collect(
        groupingBy(classifier, collectingAndThen(Collectors.toList(), l -> then.apply(Streamed.of(l)))));
    return Dictionary.of(collect);
  }

}
