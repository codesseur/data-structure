package com.codesseur.iterate.container;

import com.codesseur.iterate.Streamed;
import com.codesseur.MicroType;

public interface Container<T, C extends Iterable<T>> extends Streamed<T>, MicroType<C> {

  C value();

}
