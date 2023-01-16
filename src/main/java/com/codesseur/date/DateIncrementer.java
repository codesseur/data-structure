package com.codesseur.date;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Optional;

public class DateIncrementer implements DateOperation {

  private final TemporalAmount increment;

  public DateIncrementer(TemporalAmount increment) {
    this.increment = increment;
  }

  public TemporalAmount increment() {
    return increment;
  }

  @Override
  public Optional<LocalDateTime> apply(LocalDateTime dateTime) {
    return Optional.of(dateTime.plus(increment()));
  }
}
