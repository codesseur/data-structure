package com.codesseur.date;

import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoUnit.DAYS;

import java.time.DayOfWeek;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.EnumSet;
import java.util.Set;

public class WorkingDaysAdjuster implements TemporalAdjuster {

  private static final Set<DayOfWeek> WEEKEND = EnumSet.of( DayOfWeek.SATURDAY , DayOfWeek.SUNDAY );

  @Override
  public Temporal adjustInto(Temporal temporal) {
    DayOfWeek dayOfWeek = DayOfWeek.of(temporal.get(DAY_OF_WEEK));
    return WEEKEND.contains(dayOfWeek)? temporal.plus(1, DAYS).with(this) : temporal;
  }

  TemporalAdjuster then(TemporalAdjuster temporalAdjuster) {
    return d -> d.with(this).with(temporalAdjuster);
  }

}
