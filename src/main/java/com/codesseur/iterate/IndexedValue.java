package com.codesseur.iterate;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.function.Function;

public class IndexedValue<T> {

  public IndexedValue(int index, T value, boolean last) {
    this.index = index;
    this.value = value;
    this.last = last;
  }

  public static <T> IndexedValue<T> last(int index, T value) {
    return new IndexedValue<>(index, value, true);
  }

  public static <T> IndexedValue<T> lead(int index, T value) {
    return new IndexedValue<>(index, value, false);
  }

  private final int index;
  private final T value;
  private final boolean last;

  public int index() {
    return index;
  }

  public T value() {
    return value;
  }

  public boolean last() {
    return last;
  }

  public <V> IndexedValue<V> map(Function<T, V> mapper) {
    return new IndexedValue<>(index(), mapper.apply(value()), last());
  }

  public <V> V fold(Function<? super T, ? extends V> mapper) {
    return mapper.apply(value());
  }

  public boolean isLast() {
    return last();
  }

  public boolean isIndexed(int index) {
    return index == index();
  }

  public boolean isFirst() {
    return isIndexed(0);
  }

  public boolean isUnique() {
    return isFirst() && isLast();
  }

  public Tuple2<Integer, T> tuple() {
    return Tuple.of(index(), value());
  }
}
