package com.codesseur.iterate;

import static java.util.function.Function.identity;

import com.codesseur.iterate.container.Bag;
import com.codesseur.iterate.container.Sequence;
import com.codesseur.iterate.container.SimpleBag;
import com.codesseur.iterate.container.SimpleSequence;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

public class Collect {

  public static <T1, T2, A1, A2, R1, R2> Collector<Either<T1, T2>, ?, Tuple2<R1, R2>> partition(
      Collector<? super T1, A1, R1> first,
      Collector<? super T2, A2, R2> second
  ) {
    return partition(identity(), first, second, Tuple::of);
  }

  public static <T, T1, T2, A1, A2, R1, R2> Collector<T, ?, Tuple2<R1, R2>> partition(
      Function<? super T, ? extends Either<T1, T2>> splitter,
      Collector<? super T1, A1, R1> first,
      Collector<? super T2, A2, R2> second
  ) {
    return partition(splitter, first, second, Tuple::of);
  }

  public static <T1, T2, A1, A2, R, R1, R2> Collector<Either<T1, T2>, ?, R> partition(
      Collector<? super T1, A1, R1> first,
      Collector<? super T2, A2, R2> second,
      BiFunction<R1, R2, R> joiner
  ) {
    return partition(identity(), first, second, joiner);
  }

  public static <T, T1, T2, A1, A2, R, R1, R2> Collector<T, ?, R> partition(
      Function<? super T, ? extends Either<T1, T2>> splitter,
      Collector<? super T1, A1, R1> first,
      Collector<? super T2, A2, R2> second,
      BiFunction<R1, R2, R> joiner
  ) {
    BiConsumer<A1, ? super T1> firstAccumulator = first.accumulator();
    BiConsumer<A2, ? super T2> secondAccumulator = second.accumulator();
    BinaryOperator<A1> firstCombiner = first.combiner();
    BinaryOperator<A2> secondCombiner = second.combiner();
    Function<A1, R1> firstFinisher = first.finisher();
    Function<A2, R2> secondFinisher = second.finisher();

    return new SimpleCollector<>(
        () -> Tuple.of(first.supplier().get(), second.supplier().get()),
        (tuple, o) -> splitter.apply(o).peekLeft(t -> firstAccumulator.accept(tuple._1(), t))
            .peek(f -> secondAccumulator.accept(tuple._2(), f)),
        (o1, o2) -> o1.map((it1, it2) -> o2
            .map((it11, it22) -> Tuple.of(firstCombiner.apply(it1, it11), secondCombiner.apply(it2, it22)))),
        t -> t.map(firstFinisher, secondFinisher).apply(joiner)
    );
  }

  public static <T> Collector<T, List<T>, Sequence<T>> toSequence() {
    return toSequence(SimpleSequence::new);
  }

  public static <T, S extends Sequence<T>> Collector<T, List<T>, S> toSequence(
      Function<? super List<T>, ? extends S> factory) {
    return new SimpleCollector<>(
        ArrayList::new,
        List::add,
        (l1, l2) -> {
          l1.addAll(l2);
          return l1;
        },
        factory::apply
    );
  }

  public static <T> Collector<T, Set<T>, Bag<T>> toBag() {
    return toBag(SimpleBag::new);
  }

  public static <T, B extends Bag<T>> Collector<T, Set<T>, B> toBag(Function<? super Set<T>, ? extends B> factory) {
    return new SimpleCollector<>(
        HashSet::new,
        Set::add,
        (l1, l2) -> {
          l1.addAll(l2);
          return l1;
        },
        factory::apply);
  }

  public static <K, V> Collector<Tuple2<? extends K, ? extends V>, Map<K, V>, Map<K, V>> toMapFromTuples() {
    return toMap(Tuple2::_1, Tuple2::_2);
  }

  public static <K, V> Collector<Entry<K, V>, Map<K, V>, Map<K, V>> toMapFromEntries() {
    return toMapFromEntries(identity(), identity());
  }

  public static <K, V, KK, VV> Collector<Entry<K, V>, Map<KK, VV>, Map<KK, VV>> toMapFromEntries(
      Function<? super K, ? extends KK> keyExtractor,
      Function<? super V, ? extends VV> valueExtractor) {
    return toMap(e -> keyExtractor.apply(e.getKey()), e -> valueExtractor.apply(e.getValue()));
  }

  //See Bug https://bugs.openjdk.java.net/browse/JDK-8148463
  public static <T, KK, VV> Collector<T, Map<KK, VV>, Map<KK, VV>> toMap(
      Function<? super T, ? extends KK> keyExtractor,
      Function<? super T, ? extends VV> valueExtractor) {
    return new SimpleCollector<>(
        HashMap::new,
        (m, t) -> m.put(keyExtractor.apply(t), valueExtractor.apply(t)),
        (l1, l2) -> {
          l1.putAll(l2);
          return l1;
        },
        identity());
  }

}
