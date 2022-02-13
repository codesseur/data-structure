package com.codesseur.iterate;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.codesseur.Optionals;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class Combiner<T1, T2, I1, I2, K> {

  private final Stream<T1> leftValue;
  private final Stream<T2> rightValue;
  private final Function<T1, Stream<K>> leftExtractor;
  private final Function<T2, Stream<K>> rightExtractor;
  private final Function<T1, I1> leftMapper;
  private final Function<T2, I2> rightMapper;

  public Combiner(Stream<T1> leftValue, Stream<T2> rightValue,
      Function<T1, Stream<K>> leftExtractor, Function<T2, Stream<K>> rightExtractor,
      Function<T1, I1> leftMapper, Function<T2, I2> rightMapper) {
    this.leftValue = leftValue;
    this.rightValue = rightValue;
    this.leftExtractor = leftExtractor;
    this.rightExtractor = rightExtractor;
    this.leftMapper = leftMapper;
    this.rightMapper = rightMapper;
  }

  public <II1, II2> Combiner<T1, T2, II1, II2, K> map(Function<I1, II1> leftMapper, Function<I2, II2> rightMapper) {
    return mapLeft(leftMapper).mapRight(rightMapper);
  }

  public <II1> Combiner<T1, T2, II1, I2, K> mapLeft(Function<I1, II1> mapper) {
    return new Combiner<>(leftValue, rightValue, leftExtractor, rightExtractor, leftMapper.andThen(mapper),
        rightMapper);
  }

  public <II2> Combiner<T1, T2, I1, II2, K> mapRight(Function<I2, II2> mapper) {
    return new Combiner<>(leftValue, rightValue, leftExtractor, rightExtractor, leftMapper,
        rightMapper.andThen(mapper));
  }

  public <Z> Streamed<Z> combineAll(BiFunction<I1, I2, Z> combiner) {
    return combine(l -> combiner.apply(l, null), combiner, r -> combiner.apply(null, r));
  }

  public <Z> Streamed<Z> combineBoth(BiFunction<I1, I2, Z> both) {
    return combine(l -> Stream.<Z>empty(), both.andThen(Stream::of), r -> Stream.<Z>empty())
        .flatMap(identity());
  }

  public <Z> Streamed<Z> combineLeft(Function<I1, Z> left) {
    return combineLeft(left, (o1, o2) -> left.apply(o1));
  }

  public <Z> Streamed<Z> combineLeftOnly(Function<I1, Z> left) {
    return combine(left.andThen(Optional::of), (o1, o2) -> Optional.<Z>empty(), i -> Optional.<Z>empty())
        .flatMap(Optionals::stream);
  }

  public <Z> Streamed<Z> combineLeft(Function<I1, Z> left, BiFunction<I1, I2, Z> both) {
    return combine(left.andThen(Optional::of), both.andThen(Optional::of), i -> Optional.<Z>empty())
        .flatMap(Optionals::stream);
  }

  public <Z> Streamed<Z> combineRight(Function<I2, Z> right) {
    return combineRight((o1, o2) -> right.apply(o2), right);
  }

  public <Z> Streamed<Z> combineRightOnly(Function<I2, Z> right) {
    return combine(i -> Optional.<Z>empty(), (o1, o2) -> Optional.<Z>empty(), right.andThen(Optional::of))
        .flatMap(Optionals::stream);
  }

  public <Z> Streamed<Z> combineRight(BiFunction<I1, I2, Z> both, Function<I2, Z> right) {
    return combine(i -> Optional.<Z>empty(), both.andThen(Optional::of), right.andThen(Optional::of))
        .flatMap(Optionals::stream);
  }

  public <Z> Streamed<Z> combine(Function<I1, Z> left, BiFunction<I1, I2, Z> both, Function<I2, Z> right) {
    Map<K, List<Merger<K, T1, T2, Z>>> map1 = extract(leftValue, leftExtractor,
        (e, t1) -> new Merger<>(e, t1, null, (i1, i2) -> left.compose(leftMapper).apply(i1)));
    Map<K, List<Merger<K, T1, T2, Z>>> map2 = extract(rightValue, rightExtractor,
        (e, t2) -> new Merger<>(e, null, t2, (i1, i2) -> right.compose(rightMapper).apply(i2)));

    map2.forEach((e, t) -> map1.merge(e, t,
        (list1, list2) -> cross(list1, list2).map(p -> p.apply((m1, m2) -> m1.merge(m2, (t1, t2) -> both.apply(
            leftMapper.apply(t1), rightMapper.apply(t2)))))
            .collect(toList())));
    return Streamed.of(map1.values().stream().flatMap(Collection::stream).map(Merger::get));
  }

  private <Z, T> Map<K, List<Merger<K, T1, T2, Z>>> extract(
      Stream<T> values,
      Function<T, Stream<K>> extractor,
      BiFunction<K, T, Merger<K, T1, T2, Z>> combiner) {
    return values
        .flatMap(v1 -> extractor.apply(v1).map(e1 -> combiner.apply(e1, v1)))
        .collect(groupingBy(Merger::key));
  }

  <O1, O2> Stream<Tuple2<O1, O2>> cross(List<O1> list1, List<O2> list2) {
    return list1.stream().flatMap(e1 -> list2.stream().map(e2 -> Tuple.of(e1, e2)));
  }

}
