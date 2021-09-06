package com.codesseur.iterate;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum MappingPriority {
  FIRST((f, l) -> f), LAST((f, l) -> l);

  final BiFunction<Function<?, ?>, Function<?, ?>, Function<?, ?>> picker;

  MappingPriority(BiFunction<Function<?, ?>, Function<?, ?>, Function<?, ?>> picker) {
    this.picker = picker;
  }

  <T, E> Function<? super T, ? extends E> pick(Function<? super T, ? extends E> first,
      Function<? super T, ? extends E> last) {
    return (Function<? super T, ? extends E>) picker.apply(first, last);
  }

  <T, E> E handle(Indexed<T> value, Function<? super T, ? extends E> first,
      Function<? super T, ? extends E> middle, Function<? super T, ? extends E> last) {
    if (value.isUnique()) {
      return value.fold(pick(first, last));
    } else if (value.isFirst()) {
      return value.fold(first);
    } else if (value.isLast()) {
      return value.fold(last);
    } else {
      return value.fold(middle);
    }
  }
}
