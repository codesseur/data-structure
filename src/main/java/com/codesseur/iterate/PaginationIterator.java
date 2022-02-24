package com.codesseur.iterate;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

class PaginationIterator<O, T> implements Iterator<T> {

  private final Function<O, List<T>> next;
  private final Function<T, O> offsetExtractor;
  private O nextOffset;
  private Iterator<T> iterator = Collections.emptyIterator();

  public PaginationIterator(O zero, Function<O, List<T>> next, Function<T, O> offsetExtractor) {
    this.nextOffset = zero;
    this.next = next;
    this.offsetExtractor = offsetExtractor;
  }

  @Override
  public boolean hasNext() {
    if (!iterator.hasNext()) {
      List<T> items = Optional.ofNullable(next.apply(nextOffset)).orElseGet(Collections::emptyList);
      if (items.isEmpty()) {
        return false;
      }
      nextOffset = offsetExtractor.apply(items.get(items.size() - 1));
      iterator = items.iterator();
      return iterator.hasNext();
    }
    return true;
  }

  @Override
  public T next() {
    return iterator.next();
  }
}