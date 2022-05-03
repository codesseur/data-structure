package com.codesseur.iterate;

import com.codesseur.iterate.container.Sequence;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShiftingIterator<T> implements Iterator<Sequence<T>> {

  private final Iterator<T> origin;
  private final int size;
  private List<T> values = new ArrayList<>();

  public ShiftingIterator(Iterator<T> origin, int size) {
    if (size < 0) {
      throw new IllegalArgumentException("size must be greater than 0");
    }
    this.origin = origin;
    this.size = size;
  }

  @Override
  public boolean hasNext() {
    return !values.isEmpty() || origin.hasNext();
  }

  @Override
  public Sequence<T> next() {
    List<T> values = new ArrayList<>(this.values);
    while (origin.hasNext() && values.size() < size) {
      values.add(origin.next());
    }
    this.values = new ArrayList<>(values.subList(1, values.size()));
    return Sequence.of(values);
  }

}