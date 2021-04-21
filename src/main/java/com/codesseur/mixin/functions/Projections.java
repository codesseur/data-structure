package com.codesseur.mixin.functions;

import java.util.function.BiFunction;

public interface Projections {

  static <T, E> BiFunction<T, E, T> first() {
    return (t, e) -> t;
  }

  static <T, E> BiFunction<T, E, E> second() {
    return (t, e) -> e;
  }
}
