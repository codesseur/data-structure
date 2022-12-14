package com.codesseur.iterate.join;

import com.codesseur.iterate.Streamed;
import java.util.function.Function;

public class RightJoin<T1, T2, K> {

  final Function<T1, Streamed<K>> leftExtractor;
  final Function<T2, Streamed<K>> rightExtractor;

  public RightJoin(Function<T1, Streamed<K>> leftExtractor, Function<T2, Streamed<K>> rightExtractor) {
    this.leftExtractor = leftExtractor;
    this.rightExtractor = rightExtractor;
  }

  public RightJoin<T1, T2, K> right(Function<T2, K> extractor) {
    return rightMulti(extractor.andThen(Streamed::of));
  }

  public RightJoin<T1, T2, K> rightMulti(Function<T2, Streamed<K>> extractor) {
    return new RightJoin<>(leftExtractor, extractor);
  }
}
