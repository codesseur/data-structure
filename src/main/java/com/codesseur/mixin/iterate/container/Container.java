package com.codesseur.mixin.iterate.container;

import com.codesseur.mixin.iterate.Streamed;
import com.codesseur.mixin.MicroType;

public interface Container<T, C extends Iterable<T>> extends Streamed<T>, MicroType<C> {

  C value();

}
