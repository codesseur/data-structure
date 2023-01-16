package com.codesseur.date;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalAmount;
import java.util.Optional;
import java.util.function.Function;

public class RecurrentDates {

  private final LocalDateTime dateTime;

  private RecurrentDates(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public static RecurrentDates from(LocalDateTime dateTime) {
    return new RecurrentDates(dateTime);
  }

  public CalendarBuilder every(int amount, Cycle cycle) {
    return new CalendarBuilder(amount, cycle);
  }

  public RecurrentDate build() {
    DateOperation operation = d -> Optional.empty();
    return new RecurrentDate.Builder().origin(dateTime).dateOperation(operation).build();
  }

  public class CalendarBuilder {

    private final int amount;
    private final Cycle cycle;
    private TemporalAdjuster adjuster = d -> d;

    public CalendarBuilder(int amount, Cycle cycle) {
      this.amount = amount;
      this.cycle = cycle;
    }

    public CalendarBuilder onTheLastDayOfMonth() {
      this.adjuster = d -> (LocalDateTime) TemporalAdjusters.lastDayOfMonth().adjustInto(d);
      return this;
    }

    public RecurrentDate build() {
      RecurrentDate.Builder builder = new RecurrentDate.Builder().origin(dateTime);
      DateIncrementer operation = new DateIncrementer(cycle.apply(amount));
      builder = builder.dateOperation(operation).adjuster(adjuster);
      return Optional.of(cycle)
          .filter(Cycle.WORKING_DAY::equals)
          .map(i -> new WorkingDaysAdjuster())
          .map(builder::sanitizer)
          .orElse(builder).build();
    }
  }

  public enum Cycle implements Function<Integer, TemporalAmount> {
    DAY(Period::ofDays),
    WEEK(Period::ofWeeks),
    MONTH(Period::ofMonths),
    YEAR(Period::ofYears),
    WORKING_DAY(WorkingDaysTemporalAmount::new);

    private final Function<Integer, TemporalAmount> factory;

    Cycle(Function<Integer, TemporalAmount> factory) {
      this.factory = factory;
    }

    @Override
    public TemporalAmount apply(Integer integer) {
      return factory.apply(integer);
    }
  }

}
