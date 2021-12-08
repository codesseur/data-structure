package com.codesseur.iterate;

import io.vavr.control.Either;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class SplitIterator<T> implements Iterator<Iterator<T>> {

  private final Iterator<T> origin;
  private final Predicate<T> splitter;
  private final Function<T, Optional<Either<T, T>>> junction;
  private PartIterator<T> part;

  public SplitIterator(Iterator<T> origin, Predicate<T> splitter, Function<T, Optional<Either<T, T>>> junction) {
    this.origin = origin;
    this.splitter = splitter;
    this.junction = junction;
  }

  @Override
  public boolean hasNext() {
    return origin.hasNext() || !part.remain.isEmpty();
  }

  @Override
  public Iterator<T> next() {
    List<T> remain = List.of();
    if (part != null) {
      while (part.hasNext()) {
        part.next();
      }
      remain = part.remain;
    }

    PartIterator<T> part = new PartIterator<>(origin, splitter, junction, remain);
    return this.part = part;
  }

  public static class PartIterator<T> implements Iterator<T> {

    private final Iterator<T> origin;
    private final Iterator<T> previous;
    private final Predicate<T> stopper;
    private final Function<T, Optional<Either<T, T>>> stopOn;
    private final List<T> remain = new ArrayList<>();
    private boolean bufferConsumed = true;
    private T current;
    private boolean stop;

    public PartIterator(Iterator<T> origin, Predicate<T> stopper, Function<T, Optional<Either<T, T>>> stopOn,
        List<T> previous) {
      this.origin = origin;
      this.stopper = stopper;
      this.stopOn = stopOn;
      this.previous = previous.iterator();
    }

    @Override
    public boolean hasNext() {
      if (!bufferConsumed) {
        return true;
      } else if (stop) {
        return false;
      } else if (previous.hasNext()) {
        current = previous.next();
        bufferConsumed = false;
        return true;
      } else if (origin.hasNext()) {
        current = origin.next();
        bufferConsumed = false;
        if (stopper.test(current)) {
          stop = true;
          return stopOn.apply(current).map(e ->
              e.fold(left -> {
                current = left;
                return true;
              }, right -> {
                bufferConsumed = true;
                remain.add(right);
                return false;
              })).orElseGet(() -> {
            bufferConsumed = true;
            return false;
          });
        }
        return true;
      } else {
        return false;
      }
    }

    @Override
    public T next() {
      bufferConsumed = true;
      return current;
    }
  }

}