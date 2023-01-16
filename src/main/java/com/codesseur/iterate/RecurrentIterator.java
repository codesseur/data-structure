package com.codesseur.iterate;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class RecurrentIterator<T> implements Iterator<T> {

  private Indexed<T> nextElement;
  private final Function<Indexed<T>, Optional<T>> next;
  private final Predicate<Indexed<T>> ongoing;

  public RecurrentIterator(T zero, Function<Indexed<T>, Optional<T>> next, Predicate<Indexed<T>> ongoing) {
    this.nextElement = Optional.of(Indexed.lead(0, zero)).filter(ongoing).orElse(null);
    this.next = next;
    this.ongoing = ongoing;
  }

  @Override
  public boolean hasNext() {
    return nextElement != null;
  }

  @Override
  public T next() {
    return Optional.ofNullable(nextElement)
        .map(i -> {
          nextElement = next.apply(i)
              .map(v -> Indexed.lead(i.index() + 1, v))
              .filter(ongoing)
              .orElse(null);
          return i.value();
        }).orElse(null);
  }
}
