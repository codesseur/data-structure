package com.codesseur.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Conditions {

  public static Predicate<RecurrentDate> isBefore(LocalDateTime localDateTime) {
    return date -> date.dateTime().isBefore(localDateTime);
  }

  public static Predicate<RecurrentDate> isAfterOrEqual(LocalDateTime localDateTime) {
    return date -> date.dateTime().compareTo(localDateTime) >= 0;
  }

  public static Predicate<RecurrentDate> isAfterOrEqual(LocalDate localDate) {
    return date -> date.dateTime().toLocalDate().compareTo(localDate) >= 0;
  }

  public static Predicate<RecurrentDate> isInThePast() {
    return date -> date.dateTime().isBefore(LocalDateTime.now());
  }

  public static Predicate<RecurrentDate> isInTheFuture() {
    return date -> date.dateTime().isAfter(LocalDateTime.now());
  }

  public static BiPredicate<RecurrentDate, Integer> times(int times) {
    return (date, index) -> index < times;
  }

}
