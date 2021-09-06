package com.codesseur.iterate;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.function.Function;

public class Indexed<T> {

  private final long index;
  private final T value;
  private final boolean last;

  public Indexed(long index, T value, boolean last) {
    this.index = index;
    this.value = value;
    this.last = last;
  }

  public static <T> Indexed<T> last(long index, T value) {
    return new Indexed<>(index, value, true);
  }

  public static <T> Indexed<T> lead(long index, T value) {
    return new Indexed<>(index, value, false);
  }

  public long index() {
    return index;
  }

  public T value() {
    return value;
  }

  public <V> Indexed<V> map(Function<T, V> mapper) {
    return new Indexed<>(index(), mapper.apply(value()), last);
  }

  public <V> V fold(Function<? super T, ? extends V> mapper) {
    return mapper.apply(value());
  }

  public boolean isLast() {
    return last;
  }

  public boolean isAt(long index) {
    return index == index();
  }

  public boolean isFirst() {
    return isAt(0);
  }

  public boolean isUnique() {
    return isFirst() && isLast();
  }

  public Tuple2<Long, T> tuple() {
    return Tuple.of(index(), value());
  }
}
