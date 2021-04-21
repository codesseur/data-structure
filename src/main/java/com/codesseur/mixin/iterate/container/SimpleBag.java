package com.codesseur.mixin.iterate.container;

import java.util.Set;

public class SimpleBag<T> extends Value<Set<T>> implements Bag<T> {

  public SimpleBag(Set<T> value) {
    super(value);
  }

}
