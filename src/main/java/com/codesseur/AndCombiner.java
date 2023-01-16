package com.codesseur;

import com.codesseur.iterate.Streamed;
import com.codesseur.iterate.container.Sequence;
import io.vavr.Function3;
import io.vavr.Function4;
import io.vavr.Function5;
import io.vavr.Function6;
import io.vavr.Function7;
import io.vavr.Function8;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AndCombiner {

  private final $ values;

  public AndCombiner(Optional<?>... values) {
    this(List.of(values));
  }

  public AndCombiner(List<Optional<?>> values) {
    Sequence<?> nonEmpty = Streamed.nonEmptyOf(values).toSequence();
    this.values = nonEmpty.size() == values.size() ? new $(nonEmpty.value()) : null;
  }

  public <T> Optional<T> apply(Function<$, T> mapper) {
    return Optional.ofNullable(values).map(mapper);
  }

  public static class AndCombiner2<T1, T2> {

    private final AndCombiner combiner;

    public AndCombiner2(Optional<T1> o1, Optional<T2> o2) {
      combiner = new AndCombiner(o1, o2);
    }

    public <T> Optional<T> apply(BiFunction<T1, T2, T> mapper) {
      return combiner.apply($ -> mapper.apply($.$(0), $.$(1)));
    }
  }

  public static class AndCombiner3<T1, T2, T3> {

    private final AndCombiner combiner;

    public AndCombiner3(Optional<T1> o1, Optional<T2> o2, Optional<T3> o3) {
      combiner = new AndCombiner(o1, o2, o3);
    }

    public <T> Optional<T> apply(Function3<T1, T2, T3, T> mapper) {
      return combiner.apply($ -> mapper.apply($.$(0), $.$(1), $.$(2)));
    }
  }

  public static class AndCombiner4<T1, T2, T3, T4> {

    private final AndCombiner combiner;

    public AndCombiner4(Optional<T1> o1, Optional<T2> o2, Optional<T3> o3, Optional<T4> o4) {
      combiner = new AndCombiner(o1, o2, o3, o4);
    }

    public <T> Optional<T> apply(Function4<T1, T2, T3, T4, T> mapper) {
      return combiner.apply($ -> mapper.apply($.$(0), $.$(1), $.$(2), $.$(3)));
    }
  }

  public static class AndCombiner5<T1, T2, T3, T4, T5> {

    private final AndCombiner combiner;

    public AndCombiner5(Optional<T1> o1, Optional<T2> o2, Optional<T3> o3, Optional<T4> o4, Optional<T5> o5) {
      combiner = new AndCombiner(o1, o2, o3, o4, o5);
    }

    public <T> Optional<T> apply(Function5<T1, T2, T3, T4, T5, T> mapper) {
      return combiner.apply($ -> mapper.apply($.$(0), $.$(1), $.$(2), $.$(3), $.$(4)));
    }
  }

  public static class AndCombiner6<T1, T2, T3, T4, T5, T6> {

    private final AndCombiner combiner;

    public AndCombiner6(Optional<T1> o1, Optional<T2> o2, Optional<T3> o3, Optional<T4> o4, Optional<T5> o5,
        Optional<T6> o6) {
      combiner = new AndCombiner(o1, o2, o3, o4, o5, o6);
    }

    public <T> Optional<T> apply(Function6<T1, T2, T3, T4, T5, T6, T> mapper) {
      return combiner.apply($ -> mapper.apply($.$(0), $.$(1), $.$(2), $.$(3), $.$(4), $.$(5)));
    }
  }

  public static class AndCombiner7<T1, T2, T3, T4, T5, T6, T7> {

    private final AndCombiner combiner;

    public AndCombiner7(Optional<T1> o1, Optional<T2> o2, Optional<T3> o3, Optional<T4> o4, Optional<T5> o5,
        Optional<T6> o6,
        Optional<T7> o7) {
      combiner = new AndCombiner(o1, o2, o3, o4, o5, o6, o7);
    }

    public <T> Optional<T> apply(Function7<T1, T2, T3, T4, T5, T6, T7, T> mapper) {
      return combiner.apply($ -> mapper.apply($.$(0), $.$(1), $.$(2), $.$(3), $.$(4), $.$(5), $.$(6)));
    }
  }

  public static class AndCombiner8<T1, T2, T3, T4, T5, T6, T7, T8> {

    private final AndCombiner combiner;

    public AndCombiner8(Optional<T1> o1, Optional<T2> o2, Optional<T3> o3, Optional<T4> o4, Optional<T5> o5,
        Optional<T6> o6, Optional<T7> o7, Optional<T8> o8) {
      combiner = new AndCombiner(o1, o2, o3, o4, o5, o6, o7, o8);
    }

    public <T> Optional<T> apply(Function8<T1, T2, T3, T4, T5, T6, T7, T6, T> mapper) {
      return combiner.apply($ -> mapper.apply($.$(0), $.$(1), $.$(2), $.$(3), $.$(4), $.$(5), $.$(6), $.$(7)));
    }
  }

  public static class $ {

    private final List<?> values;

    public $(List<?> values) {
      this.values = values;
    }

    public <T> T $(int i) {
      return (T) values.get(i);
    }
  }
}
