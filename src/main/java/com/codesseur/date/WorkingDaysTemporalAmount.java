package com.codesseur.date;

import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class WorkingDaysTemporalAmount implements TemporalAmount {

  private static final Period ONE_DAY = Period.ofDays(1);

  private final int days;

  public WorkingDaysTemporalAmount(int days) {
    this.days = days;
  }

  public int days() {
    return days;
  }

  @Override
  public Temporal addTo(Temporal temporal) {
    return applyTemporalAmount(temporal, Temporal::plus);
  }

  @Override
  public long get(TemporalUnit unit) {
    return days();
  }

  @Override
  public List<TemporalUnit> getUnits() {
    return Collections.singletonList(ChronoUnit.DAYS);
  }

  @Override
  public Temporal subtractFrom(Temporal temporal) {
    return applyTemporalAmount(temporal, Temporal::minus);
  }

  private Temporal applyTemporalAmount(Temporal temporal,
      BiFunction<Temporal, TemporalAmount, Temporal> operation) {
    return IntStream.range(0, days())
        .mapToObj(i -> null)
        .reduce(temporal, (t, s) -> operation.apply(t, ONE_DAY).with(new WorkingDaysAdjuster()),
            (t1, t2) -> t2);
  }

}
