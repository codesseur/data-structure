package com.codesseur.iterate.container;

import com.codesseur.MicroType;
import com.codesseur.iterate.Streamed;

public interface Container<T, C extends Iterable<T>> extends Streamed<T>, MicroType<C> {

}
