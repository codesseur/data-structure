package com.codesseur.mixin;

public interface Enhanced<T> {

  default MicroType<T> then() {
    return () -> (T) this;
  }

}
