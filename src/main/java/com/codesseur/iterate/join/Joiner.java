package com.codesseur.iterate.join;

import com.codesseur.iterate.Streamed;
import java.util.Optional;
import java.util.function.Function;

public class Joiner<T1, T2> {

  private final Streamed<T1> leftValue;
  private final Streamed<T2> rightValue;

  public Joiner(Streamed<T1> leftValue, Streamed<T2> rightValue) {
    this.leftValue = leftValue;
    this.rightValue = rightValue;
  }

  public <K> Combiner<T1, T2, T1, T2, K> by(Function<T1, K> leftExtractor, Function<T2, K> rightExtractor) {
    return by(j -> j.left(leftExtractor).right(rightExtractor)).singles();
  }

  public <K> Combiner<T1, T2, Streamed<T1>, Streamed<T2>, K> by(
      Function<LeftJoin<T1, T2>, RightJoin<T1, T2, K>> extractor) {
    RightJoin<T1, T2, K> join = extractor.apply(new LeftJoin<>());
    return new Combiner<>(
        leftValue, rightValue,
        join.leftExtractor, join.rightExtractor,
        Optional::ofNullable, Optional::ofNullable);
  }

}
