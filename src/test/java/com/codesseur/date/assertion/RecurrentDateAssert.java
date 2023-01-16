package com.codesseur.date.assertion;

import com.codesseur.date.RecurrentDate;
import com.codesseur.date.RecurrentDate.Criteria;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class RecurrentDateAssert extends
    AbstractAssert<RecurrentDateAssert, RecurrentDate> {

  public RecurrentDateAssert(RecurrentDate actual) {
    super(actual, RecurrentDateAssert.class);
  }

  public RecurrentDateAssert hasDateTime(LocalDateTime expected) {
    Assertions.assertThat(actual.dateTime()).isEqualTo(expected);
    return this;
  }

  public RecurrentDateAssert hasNext(LocalDateTime expected) {
    has(RecurrentDate::next, expected);
    return this;
  }

  public RecurrentDateAssert hasNextFuture(int next, LocalDateTime expected) {
    has(r -> r.stream(Criteria.take(next).from(LocalDateTime.now())).last(), expected);
    return this;
  }

  public RecurrentDateAssert hasNextFuture(LocalDateTime expected) {
    has(r -> r.next(LocalDateTime.now()), expected);
    return this;
  }

  private RecurrentDateAssert has(Function<RecurrentDate, Optional<RecurrentDate>> next, LocalDateTime expected) {
    Assertions.assertThat(next.apply(actual))
        .hasValueSatisfying(v -> DateAssertions.assertThat(v).hasDateTime(expected));
    return this;
  }

}
