package com.codesseur.iterate;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.Iterator;
import java.util.Optional;

public class ZippedIterator<K, V> implements Iterator<Indexed<Tuple2<Optional<K>, Optional<V>>>> {

  private long index = -1;

  private final Iterator<K> first;
  private final Iterator<V> second;
  private final ZipMode zipMode;

  public ZippedIterator(Iterator<K> first, Iterator<V> second, ZipMode zipMode) {
    this.first = first;
    this.second = second;
    this.zipMode = zipMode;
  }

  @Override
  public Indexed<Tuple2<Optional<K>, Optional<V>>> next() {
    index++;
    Optional<K> first = getNext(this.first);
    Optional<V> second = getNext(this.second);
    return new Indexed<>(index, Tuple.of(first, second), !hasNext());
  }

  <E> Optional<E> getNext(Iterator<E> iterator) {
    return Optional.of(iterator).filter(Iterator::hasNext).map(Iterator::next);
  }

  @Override
  public boolean hasNext() {
    return zipMode.test(first.hasNext(), second.hasNext());
  }
}