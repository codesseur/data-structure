package com.codesseur.iterate;

import com.codesseur.Optionals;
import java.util.function.BiFunction;

public class Merger<E, T1, T2, Z> {

  private final E key;
  private final T1 v1;
  private final T2 v2;
  private final BiFunction<T1, T2, Z> applier;

  public Merger(E key, T1 v1, T2 v2, BiFunction<T1, T2, Z> applier) {
    this.key = key;
    this.v1 = v1;
    this.v2 = v2;
    this.applier = applier;
  }

  E key() {
    return key;
  }

  Z get() {
    return applier.apply(v1, v2);
  }

  public Merger<E, T1, T2, Z> merge(Merger<E, T1, T2, Z> merger, BiFunction<T1, T2, Z> combiner) {
    return new Merger<>(key,
        Optionals.or(v1, merger.v1).orElse(null),
        Optionals.or(v2, merger.v2).orElse(null),
        combiner);
  }
}
