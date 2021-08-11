package com.codesseur.iterate;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toSet;

import com.codesseur.iterate.container.Bag;
import com.codesseur.iterate.container.Sequence;
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

  public static <T1, T2, R1, R2> Collector<Either<T1, T2>, ?, Tuple2<R1,R2>> partitionSets(Function<Set<T1>, R1> left, Function<Set<T2>, R2> right) {
    return partition(toSet(), toSet(), (t1, t2) -> Tuple.of(t1, t2).map(left, right));
  }

  public static <T1, T2, R> Collector<Either<T1, T2>, ?, R> partitionSets(BiFunction<Set<T1>, Set<T2>, R> joiner) {
    return partition(toSet(), toSet(), joiner);
  }

  public static <T1, R1, T2, R2, R> Collector<Either<T1, T2>, Tuple2<?, ?>, R> partition(
      Collector<T1, ?, R1> first,
      Collector<T2, ?, R2> second,
      BiFunction<R1, R2, R> joiner) {
    BiConsumer<Object, T1> firstAccumulator = (BiConsumer<Object, T1>) first.accumulator();
    BiConsumer<Object, T2> secondAccumulator = (BiConsumer<Object, T2>) second.accumulator();
    BinaryOperator<Object> firstCombiner = (BinaryOperator<Object>) first.combiner();
    BinaryOperator<Object> secondCombiner = (BinaryOperator<Object>) second.combiner();
    Function<Object, R1> firstFinisher = (Function<Object, R1>) first.finisher();
    Function<Object, R2> secondFinisher = (Function<Object, R2>) second.finisher();
    return new SimpleCollector<>(
        () -> Tuple.of(first.supplier().get(), second.supplier().get()),
        (tuple, either) -> either.peekLeft(t -> firstAccumulator.accept(tuple._1(), t))
            .peek(f -> secondAccumulator.accept(tuple._2(), f)),
        (o1, o2) -> o1.map((it1, it2) -> o2
            .map((it11, it22) -> Tuple.of(firstCombiner.apply(it1, it11), secondCombiner.apply(it2, it22)))),
        t -> t.map(firstFinisher, secondFinisher).apply(joiner)
    );
  }

  public static <T> Collector<T, List<T>, Sequence<T>> toSequence() {
    return toSequence(Sequence::of);
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
    return toBag(Bag::of);
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
      Function<? super K, ? extends KK> keyMapper,
      Function<? super V, ? extends VV> valueMapper) {
    return toMap(e -> keyMapper.apply(e.getKey()), e -> valueMapper.apply(e.getValue()));
  }

  //See Bug https://bugs.openjdk.java.net/browse/JDK-8148463
  public static <T, KK, VV> Collector<T, Map<KK, VV>, Map<KK, VV>> toMap(
      Function<? super T, ? extends KK> keyMapper,
      Function<? super T, ? extends VV> valueMapper) {
    return new SimpleCollector<>(
        HashMap::new,
        (m, t) -> m.put(keyMapper.apply(t), valueMapper.apply(t)),
        (l1, l2) -> {
          l1.putAll(l2);
          return l1;
        },
        identity());
  }

}
