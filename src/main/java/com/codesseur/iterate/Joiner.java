package com.codesseur.iterate;

import static java.util.function.Function.identity;

import java.util.function.Function;
import java.util.stream.Stream;

public class Joiner<T1, T2> {

  private final Stream<T1> value1;
  private final Stream<T2> value2;

  public Joiner(Stream<T1> value1, Stream<T2> value2) {
    this.value1 = value1;
    this.value2 = value2;
  }

  public Combiner<T1, T2, T1, T2, T2> byRight(Function<T1, T2> extractor) {
    return by(extractor, identity());
  }

  public Combiner<T1, T2, T1, T2, T1> byLeft(Function<T2, T1> extractor) {
    return by(identity(), extractor);
  }

  public <E> Combiner<T1, T2, T1, T2, E> by(Function<T1, E> extractor1, Function<T2, E> extractor2) {
    return byMulti(t1 -> Stream.of(extractor1.apply(t1)), t2 -> Stream.of(extractor2.apply(t2)));
  }

  public <E> Combiner<T1, T2, T1, T2, E> byMulti(Function<T1, Stream<E>> extractor1,
      Function<T2, Stream<E>> extractor2) {
    return new Combiner<>(value1, value2, extractor1, extractor2, Function.identity(), Function.identity());
  }

}
