package com.codesseur.iterate;

import com.codesseur.iterate.container.Sequence;
import io.vavr.control.Either;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class SplittingIterator<T> implements Iterator<Sequence<T>> {

  private final Iterator<T> origin;
  private final Predicate<Indexed<T>> splitter;
  private final Function<T, Optional<Either<T, T>>> junction;
  private List<T> remaining = new ArrayList<>();
  private long index;

  public SplittingIterator(Iterator<T> origin, Predicate<Indexed<T>> splitter,
      Function<T, Optional<Either<T, T>>> junction) {
    this.origin = origin;
    this.splitter = splitter;
    this.junction = junction;
  }

  @Override
  public boolean hasNext() {
    return !remaining.isEmpty() || origin.hasNext();
  }

  @Override
  public Sequence<T> next() {
    List<T> values = remaining;
    remaining = new ArrayList<>();
    boolean proceed = true;
    while (origin.hasNext() && proceed) {
      T next = origin.next();
      if (splitter.test(new Indexed<>(index, next, !origin.hasNext()))) {
        proceed = false;
        junction.apply(next).ifPresent(e -> e.peek(remaining::add).peekLeft(values::add));
      } else {
        values.add(next);
      }
      index++;
    }
    return Sequence.of(values);
  }

}