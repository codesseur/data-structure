package com.codesseur.iterate.join;

import com.codesseur.iterate.Streamed;
import java.util.function.Function;

public class LeftJoin<T1, T2> {

  public <K> RightJoin<T1, T2, K> left(Function<T1, K> extractor) {
    return leftMulti(extractor.andThen(Streamed::of));
  }

  public <K> RightJoin<T1, T2, K> leftMulti(Function<T1, Streamed<K>> extractor) {
    return new RightJoin<>(extractor, v -> Streamed.empty());
  }

}
