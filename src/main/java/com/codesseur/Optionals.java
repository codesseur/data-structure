package com.codesseur;

import com.codesseur.AndCombiner.AndCombiner2;
import com.codesseur.AndCombiner.AndCombiner3;
import com.codesseur.AndCombiner.AndCombiner4;
import com.codesseur.AndCombiner.AndCombiner5;
import com.codesseur.AndCombiner.AndCombiner6;
import com.codesseur.AndCombiner.AndCombiner7;
import com.codesseur.AndCombiner.AndCombiner8;
import com.codesseur.iterate.Streamed;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.control.Either;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Optionals {

  @SafeVarargs
  static <T> Stream<T> stream(Supplier<Optional<? extends T>>... optionals) {
    return Stream.of(optionals).map(Supplier::get).flatMap(Optionals::stream);
  }

  static <T> Stream<T> stream(Optional<? extends T> optional) {
    return optional.stream().map(t -> (T) t);
  }

  static <T1, T2> AndCombiner2<T1, T2> and(Optional<T1> o1, Optional<T2> o2) {
    return new AndCombiner2<>(o1, o2);
  }

  static <T1, T2, T3> AndCombiner3<T1, T2, T3> and(Optional<T1> o1, Optional<T2> o2, Optional<T3> o3) {
    return new AndCombiner3<>(o1, o2, o3);
  }

  static <T1, T2, T3, T4> AndCombiner4<T1, T2, T3, T4> and(Optional<T1> o1, Optional<T2> o2, Optional<T3> o3, Optional<T4> o4) {
    return new AndCombiner4<>(o1, o2, o3, o4);
  }

  static <T1, T2, T3, T4, T5> AndCombiner5<T1, T2, T3, T4, T5> and(Optional<T1> o1, Optional<T2> o2, Optional<T3> o3, Optional<T4> o4, Optional<T5> o5) {
    return new AndCombiner5<>(o1, o2, o3, o4, o5);
  }

  static <T1, T2, T3, T4, T5, T6> AndCombiner6<T1, T2, T3, T4, T5, T6> and(Optional<T1> o1, Optional<T2> o2, Optional<T3> o3, Optional<T4> o4, Optional<T5> o5, Optional<T6> o6) {
    return new AndCombiner6<>(o1, o2, o3, o4, o5, o6);
  }

  static <T1, T2, T3, T4, T5, T6, T7> AndCombiner7<T1, T2, T3, T4, T5, T6, T7> and(Optional<T1> o1, Optional<T2> o2, Optional<T3> o3, Optional<T4> o4, Optional<T5> o5, Optional<T6> o6, Optional<T7> o7) {
    return new AndCombiner7<>(o1, o2, o3, o4, o5, o6, o7);
  }

  static <T1, T2, T3, T4, T5, T6, T7, T8> AndCombiner8<T1, T2, T3, T4, T5, T6, T7, T8> and(Optional<T1> o1, Optional<T2> o2, Optional<T3> o3, Optional<T4> o4, Optional<T5> o5, Optional<T6> o6, Optional<T7> o7, Optional<T8> o8) {
    return new AndCombiner8<>(o1, o2, o3, o4, o5, o6, o7, o8);
  }

  static <T, E> OptionalCombiner<T, E> join(Optional<T> o1, Optional<E> o2) {
    return new OptionalCombiner<>(o1, o2);
  }

  @SafeVarargs
  static <T> Optional<T> or(T... nullables) {
    return or(Arrays.stream(nullables).map(o -> () -> Optional.ofNullable(o)));
  }

  @SafeVarargs
  static <T> Optional<T> or(Optional<? extends T>... optionals) {
    return or(Arrays.stream(optionals).map(o -> () -> o));
  }

  @SafeVarargs
  static <T> Optional<T> or(Supplier<Optional<? extends T>>... optionals) {
    return or(Stream.of(optionals));
  }

  static <T> Optional<T> or(List<Supplier<Optional<? extends T>>> optionals) {
    return or(optionals.stream());
  }

  static <T> Optional<T> or(Stream<Supplier<Optional<? extends T>>> optionals) {
    return optionals.map(Supplier::get).flatMap(Optionals::stream).map(v -> (T) v).findFirst();
  }

  @SafeVarargs
  static <T> Optional<T> xor(Optional<? extends T>... optionals) {
    return xor(Streamed.of(optionals).<Supplier<Optional<? extends T>>>map(i -> () -> i).toList());
  }

  @SafeVarargs
  static <T> Optional<T> xor(Supplier<Optional<? extends T>>... optionals) {
    return xor(Streamed.of(optionals).toList());
  }

  static <T> Optional<T> xor(List<Supplier<Optional<? extends T>>> optionals) {
    int count = 0;
    Optional<T> result = Optional.empty();

    for (Supplier<Optional<? extends T>> o : optionals) {
      Optional<? extends T> element = o.get();
      if (element.isPresent()) {
        count++;
        result = (Optional<T>) element;
      }
      if (count > 1) {
        return Optional.empty();
      }
    }
    return result;
  }

  class SameTypeOptionalCombiner<C> extends OptionalCombiner<C, C> {

    private SameTypeOptionalCombiner(Optional<C> o1, Optional<C> o2) {
      super(o1, o2);
    }

    public <T> Optional<T> combine(BiFunction<? super C, ? super C, ? extends T> both,
        Function<? super C, ? extends T> leftOrTight) {
      return combine(leftOrTight, both, leftOrTight);
    }
  }

  class SafeOptionalCombiner<L, R> {

    private final OptionalCombiner<L, R> combiner;

    public SafeOptionalCombiner(Optional<L> o1, Optional<R> o2) {
      combiner = new OptionalCombiner<>(o1, o2);
    }

    public <T> T combine(Function<? super L, ? extends T> left,
        BiFunction<? super L, ? super R, ? extends T> both,
        Function<? super R, ? extends T> right) {
      return combiner.combine(left, both, right).orElseThrow(UnmatchedCombination::new);
    }

    public <T> T combineBothOtherwise(BiFunction<? super L, ? super R, ? extends T> both,
        Function<? super Either<L, R>, ? extends T> otherwise) {
      return combiner.combineBothOtherwise(both, otherwise).orElseThrow(UnmatchedCombination::new);
    }

    public <T> T combineBoth(BiFunction<? super L, ? super R, ? extends T> both) {
      return combiner.combineBoth(both).orElseThrow(UnmatchedCombination::new);
    }

    public <T> T combineLeft(Function<? super L, ? extends T> left) {
      return combiner.combineLeft(left).orElseThrow(UnmatchedCombination::new);
    }

    public <T> T combineRight(Function<? super R, ? extends T> right) {
      return combiner.combineRight(right).orElseThrow(UnmatchedCombination::new);
    }

    public <T> T combineLeftOrRight(Function<? super L, ? extends T> left,
        Function<? super R, ? extends T> right) {
      return combiner.combineLeftOrRight(left, right).orElseThrow(UnmatchedCombination::new);
    }

    public static class UnmatchedCombination extends RuntimeException {

    }
  }

  class OptionalCombiner<L, R> {

    private final Optional<L> o1;
    private final Optional<R> o2;

    private OptionalCombiner(Optional<L> o1, Optional<R> o2) {
      this.o1 = o1;
      this.o2 = o2;
    }

    public <T> Optional<T> combine(Function<? super L, ? extends T> left,
        BiFunction<? super L, ? super R, ? extends T> both,
        Function<? super R, ? extends T> right) {
      return or(
          () -> o1.flatMap(f -> o2.map(s -> both.andThen(Optional::ofNullable).apply(f, s))),
          () -> o1.map(left.andThen(Optional::ofNullable)),
          () -> o2.map(right.andThen(Optional::ofNullable)))
          .flatMap(Function.identity());
    }

    public <T> T combineAll(BiFunction<? super L, ? super R, ? extends T> all) {
      return combine(l -> all.apply(l, null), all, r -> all.apply(null, r))
          .orElseGet(() -> all.apply(null, null));
    }

    public <T> T combineBothOtherwise(BiFunction<? super L, ? super R, ? extends T> both,
        Supplier<? extends T> otherwise) {
      return combineBothOtherwise(both, i -> otherwise.get()).orElseGet(otherwise);
    }

    public <T> Optional<T> combineBothOtherwise(BiFunction<? super L, ? super R, ? extends T> both,
        Function<? super Either<L, R>, ? extends T> otherwise) {
      return combine(l -> otherwise.apply(Either.left(l)), both, l -> otherwise.apply(Either.right(l)));
    }

    public <T> Optional<T> combineBoth(BiFunction<? super L, ? super R, ? extends T> both) {
      return combineBothOtherwise(both.andThen(Optional::ofNullable), Optional::empty);
    }

    public <T> Optional<T> combineLeft(Function<? super L, ? extends T> left) {
      return combineLeftOrRight(left, i -> null);
    }

    public <T> Optional<T> combineRight(Function<? super R, ? extends T> right) {
      return combineLeftOrRight(i -> null, right);
    }

    public <T> Optional<T> combineLeftOrRight(Function<? super L, ? extends T> left,
        Function<? super R, ? extends T> right) {
      return combine(left, (i1, i2) -> null, right);
    }

    public SafeOptionalCombiner<L, R> uncheck() {
      return new SafeOptionalCombiner<>(o1, o2);
    }

    public SameTypeOptionalCombiner<L> sameType() {
      return new SameTypeOptionalCombiner<L>(o1, (Optional<L>) o2);
    }

  }

}
