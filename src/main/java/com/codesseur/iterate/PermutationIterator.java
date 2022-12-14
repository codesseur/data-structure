package com.codesseur.iterate;

import com.codesseur.iterate.container.Sequence;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public class PermutationIterator<T> implements Iterator<Sequence<T>> {

  private List<T> values;
  private final int size;
  private final int[] indexes;
  private int index;

  public PermutationIterator(Sequence<T> values) {
    this.values = values.value();
    size = values.size();
    indexes = new int[size];
    Arrays.fill(indexes, 0);
  }

  @Override
  public boolean hasNext() {
    boolean stop = Streamed.of(IntStream.of(indexes).boxed()).zipWithIndex().allMatch(i -> i.index() == i.value());
    return index < size && !stop;
  }

  @Override
  public Sequence<T> next() {
    while (index < size) {
      if (indexes[index] < index) {
        values = new ArrayList<>(values);
        swap(values, index % 2 == 0 ? 0 : indexes[index], index);
        indexes[index]++;
        index = 0;
        break;
      } else {
        indexes[index] = 0;
        index++;
      }
    }
    return Sequence.of(values);
  }

  private void swap(List<T> input, int a, int b) {
    T tmp = input.get(a);
    input.set(a, input.get(b));
    input.set(b, tmp);
  }

}