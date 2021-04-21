package com.codesseur.mixin.iterate.container;

import java.util.List;

public class SimpleSequence<T> extends Value<List<T>> implements Sequence<T> {

  public SimpleSequence(List<T> value) {
    super(value);
  }
}
