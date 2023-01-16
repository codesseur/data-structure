package com.codesseur.date.date;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class DateObjectMother {
  public LocalDateTime yesterday() {
    return LocalDateTime.now().minusDays(1);
  }

  public LocalDateTime today() {
    return LocalDateTime.now();
  }

  public LocalDateTime tomorrow() {
    return LocalDateTime.now().plusDays(1);
  }

  public LocalDateTime last(DayOfWeek dayOfWeek) {
    return LocalDateTime.now().minusDays(7).with(TemporalAdjusters.next(dayOfWeek));
  }

  public LocalDateTime next(DayOfWeek dayOfWeek) {
    return LocalDateTime.now().with(TemporalAdjusters.next(dayOfWeek));
  }

  public LocalDateTime nextAfter(DayOfWeek dayOfWeek, LocalDateTime dateTime) {
    return dateTime.with(TemporalAdjusters.next(dayOfWeek));
  }

  public LocalDateTime nextAfterDays(int days) {
    return LocalDateTime.now().plusDays(days);
  }

  public LocalDateTime nextAfterWeeks(int weeks) {
    return LocalDateTime.now().plusWeeks(weeks);
  }

  public LocalDateTime nextAfterMonths(int months) {
    return LocalDateTime.now().plusMonths(months);
  }

  public LocalDateTime nextLastDayOfMonthAfterMonths(int months) {
    return LocalDateTime.now().plusMonths(months).with(TemporalAdjusters.lastDayOfMonth());
  }

  public LocalDateTime nextFirstDayOfMonth() {
    return LocalDateTime.now().plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
  }

  public LocalDateTime nextLastDayOfMonthAfter(LocalDateTime reference) {
    return reference.with(TemporalAdjusters.lastDayOfMonth());
  }

  public LocalDateTime nextAfterYears(int years) {
    return LocalDateTime.now().plusYears(years);
  }

  public LocalDateTime adjustIntoWorkingDays(LocalDateTime temporal) {
    int field = temporal.get(ChronoField.DAY_OF_WEEK);
    DayOfWeek dayOfWeek = DayOfWeek.of(field);
    int daysToAdd = 0;
    if (DayOfWeek.SUNDAY.equals(dayOfWeek)) {
      daysToAdd = 1;
    } else if (DayOfWeek.SATURDAY.equals(dayOfWeek)) {
      daysToAdd = 2;
    }
    return temporal.plus(daysToAdd, ChronoUnit.DAYS);
  }

}
