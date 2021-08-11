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

public class Combiner<T1, T2, E> {

  private final Stream<T1> value1;
  private final Stream<T2> value2;
  private final Function<T1, Stream<E>> extractor1;
  private final Function<T2, Stream<E>> extractor2;

  public Combiner(Stream<T1> value1, Stream<T2> value2,
      Function<T1, Stream<E>> extractor1, Function<T2, Stream<E>> extractor2) {
    this.value1 = value1;
    this.value2 = value2;
    this.extractor1 = extractor1;
    this.extractor2 = extractor2;
  }

  public <Z> Streamed<Z> combineAll(BiFunction<T1, T2, Z> combiner) {
    return combine(l -> combiner.apply(l, null), combiner, r -> combiner.apply(null, r));
  }

  public <Z> Streamed<Z> combineBoth(BiFunction<T1, T2, Z> both) {
    return combine(l -> Stream.<Z>empty(), both.andThen(Stream::of), r -> Stream.<Z>empty())
        .flatMap(identity());
  }

  public <Z> Streamed<Z> combineLeft(Function<T1, Z> left) {
    return combineLeft(left, (o1, o2) -> left.apply(o1));
  }

  public <Z> Streamed<Z> combineLeftOnly(Function<T1, Z> left) {
    return combine(left.andThen(Optional::of), (o1, o2) -> Optional.<Z>empty(), i -> Optional.<Z>empty())
        .flatMap(Optionals::stream);
  }

  public <Z> Streamed<Z> combineLeft(Function<T1, Z> left, BiFunction<T1, T2, Z> both) {
    return combine(left.andThen(Optional::of), both.andThen(Optional::of), i -> Optional.<Z>empty())
        .flatMap(Optionals::stream);
  }

  public <Z> Streamed<Z> combineRight(Function<T2, Z> right) {
    return combineRight((o1, o2) -> right.apply(o2), right);
  }

  public <Z> Streamed<Z> combineRightOnly(Function<T2, Z> right) {
    return combine(i -> Optional.<Z>empty(), (o1, o2) -> Optional.<Z>empty(), right.andThen(Optional::of))
        .flatMap(Optionals::stream);
  }

  public <Z> Streamed<Z> combineRight(BiFunction<T1, T2, Z> both, Function<T2, Z> right) {
    return combine(i -> Optional.<Z>empty(), both.andThen(Optional::of), right.andThen(Optional::of))
        .flatMap(Optionals::stream);
  }

  public <Z> Streamed<Z> combine(Function<T1, Z> left, BiFunction<T1, T2, Z> both, Function<T2, Z> right) {
    Map<E, List<Merger<E, T1, T2, Z>>> map1 = extract(value1, extractor1,
        (e, t1) -> new Merger<>(e, t1, null, (i1, i2) -> left.apply(i1)));
    Map<E, List<Merger<E, T1, T2, Z>>> map2 = extract(value2, extractor2,
        (e, t2) -> new Merger<>(e, null, t2, (i1, i2) -> right.apply(i2)));

    map2.forEach((e, t) -> map1.merge(e, t,
        (list1, list2) -> cross(list1, list2).map(p -> p.apply((m1, m2) -> m1.merge(m2, both)))
            .collect(toList())));
    return Streamed.of(map1.values().stream().flatMap(Collection::stream).map(Merger::get));
  }

  private <Z, T> Map<E, List<Merger<E, T1, T2, Z>>> extract(
      Stream<T> values,
      Function<T, Stream<E>> extractor,
      BiFunction<E, T, Merger<E, T1, T2, Z>> combiner) {
    return values
        .flatMap(v1 -> extractor.apply(v1).map(e1 -> combiner.apply(e1, v1)))
        .collect(groupingBy(Merger::key));
  }

  <O1, O2> Stream<Tuple2<O1, O2>> cross(List<O1> list1, List<O2> list2) {
    return list1.stream().flatMap(e1 -> list2.stream().map(e2 -> Tuple.of(e1, e2)));
  }

}
