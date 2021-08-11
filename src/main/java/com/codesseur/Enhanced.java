package com.codesseur;

public interface Enhanced<T> {

  default MicroType<T> then() {
    return () -> (T) this;
  }

}
