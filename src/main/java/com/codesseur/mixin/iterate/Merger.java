package com.codesseur.mixin.iterate;

import static com.codesseur.mixin.Optionals.or;

import com.codesseur.mixin.Optionals;
import java.util.function.BiFunction;

public class Merger<E, T1, T2, Z> {

  E key;
  T1 v1;
  T2 v2;
  BiFunction<T1, T2, Z> applier;

  public Merger(E key, T1 v1, T2 v2, BiFunction<T1, T2, Z> applier) {
    this.key = key;
    this.v1 = v1;
    this.v2 = v2;
    this.applier = applier;
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
