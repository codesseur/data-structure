package com.codesseur.iterate.join;

import static io.vavr.control.Try.run;
import static java.util.function.Function.identity;

import com.codesseur.Optionals;
import com.codesseur.iterate.Streamed;
import com.codesseur.iterate.container.Dictionary;
import io.vavr.Function3;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class Combiner<T1, T2, I1, I2, K> {

  private final Streamed<T1> leftValue;
  private final Streamed<T2> rightValue;
  private final Function<T1, Streamed<K>> leftExtractor;
  private final Function<T2, Streamed<K>> rightExtractor;
  private final Function<Streamed<T1>, Optional<I1>> leftMapper;
  private final Function<Streamed<T2>, Optional<I2>> rightMapper;

  public Combiner(Streamed<T1> leftValue, Streamed<T2> rightValue, Function<T1, Streamed<K>> leftExtractor,
      Function<T2, Streamed<K>> rightExtractor, Function<Streamed<T1>, Optional<I1>> leftMapper,
      Function<Streamed<T2>, Optional<I2>> rightMapper) {
    this.leftValue = leftValue;
    this.rightValue = rightValue;
    this.leftExtractor = leftExtractor;
    this.rightExtractor = rightExtractor;
    this.leftMapper = leftMapper;
    this.rightMapper = rightMapper;
  }

  public <II1, II2> Combiner<T1, T2, II1, II2, K> map(Function<I1, II1> leftMapper,
      Function<I2, II2> rightMapper) {
    return mapLeft(leftMapper).mapRight(rightMapper);
  }

  public Combiner<T1, T2, T1, T2, K> singles() {
    return singleLeft().singleRight();
  }

  public Combiner<T1, T2, T1, I2, K> singleLeft() {
    return new Combiner<>(leftValue, rightValue, leftExtractor, rightExtractor,
        Streamed::head, rightMapper);
  }

  public Combiner<T1, T2, Streamed<T1>, I2, K> multiLeft() {
    return new Combiner<>(leftValue, rightValue, leftExtractor, rightExtractor, Optional::ofNullable, rightMapper);
  }

  public <II1> Combiner<T1, T2, II1, I2, K> mapLeft(Function<I1, II1> mapper) {
    return new Combiner<>(leftValue, rightValue, leftExtractor, rightExtractor,
        leftMapper.andThen(o -> o.map(mapper)), rightMapper);
  }

  public Combiner<T1, T2, I1, T2, K> singleRight() {
    return new Combiner<>(leftValue, rightValue, leftExtractor, rightExtractor,
        leftMapper, Streamed::head);
  }

  public Combiner<T1, T2, I1, Streamed<T2>, K> multiRight() {
    return new Combiner<>(leftValue, rightValue, leftExtractor, rightExtractor, leftMapper, Optional::ofNullable);
  }

  public <II2> Combiner<T1, T2, I1, II2, K> mapRight(Function<I2, II2> mapper) {
    return new Combiner<>(leftValue, rightValue, leftExtractor, rightExtractor,
        leftMapper, rightMapper.andThen(o -> o.map(mapper)));
  }

  public void forEachBoth(BiConsumer<I1, I2> both) {
    forEach(l -> {}, both, r -> {});
  }

  public void forEachLeftOrRightOnly(Consumer<I1> left, Consumer<I2> right) {
    forEach(left, (l, r) -> {}, right);
  }

  public void forEach(Consumer<I1> left, BiConsumer<I1, I2> both, Consumer<I2> right) {
    combine(l -> run(() -> left.accept(l)), (l, r) -> run(() -> both.accept(l, r)), r -> run(() -> right.accept(r)))
        .forEach(Try::get);
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

  public <Z> Streamed<Z> combineLeftOrRightOnly(Function<I1, Z> left, Function<I2, Z> right) {
    return combine(left.andThen(Optional::of), (l, r) -> Optional.<Z>empty(), right.andThen(Optional::of))
        .flatMap(Optionals::stream);
  }

  public <Z> Streamed<Z> combine(Function<I1, Z> left, BiFunction<I1, I2, Z> both, Function<I2, Z> right) {
    return combine((k, l) -> left.apply(l), (k, l, r) -> both.apply(l, r), (k, r) -> right.apply(r));
  }

  public <Z> Streamed<Z> combine(BiFunction<K, I1, Z> left, Function3<K, I1, I2, Z> both, BiFunction<K, I2, Z> right) {
    Dictionary<K, Tuple2<Streamed<T1>, Streamed<T2>>> leftMap = leftValue.flatMapSticky(leftExtractor)
        .groupBy(Tuple2::_2, s -> Tuple.of(s.map(Tuple2::_1), null));

    Dictionary<K, Tuple2<Streamed<T1>, Streamed<T2>>> rightMap = rightValue.flatMapSticky(rightExtractor)
        .groupBy(Tuple2::_2, s -> Tuple.of(null, s.map(Tuple2::_1)));

    return rightMap.stream()
        .foldLeft(() -> leftMap, (m1, e2) -> m1.put(e2.getKey(), v1 -> v1.update2(e2.getValue()._2), e2::getValue))
        .mapPartial((k, t) -> Optionals.join(
                Optional.ofNullable(t._1).flatMap(leftMapper),
                Optional.ofNullable(t._2).flatMap(rightMapper))
            .combine(l -> left.apply(k, l), (l, r) -> both.apply(k, l, r), r -> right.apply(k, r)));
  }

}
