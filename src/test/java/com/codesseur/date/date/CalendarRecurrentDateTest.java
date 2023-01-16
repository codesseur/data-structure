package com.codesseur.date.date;

import static com.codesseur.date.RecurrentDate.Criteria.take;
import static com.codesseur.date.RecurrentDates.Cycle.DAY;
import static com.codesseur.date.RecurrentDates.Cycle.MONTH;
import static com.codesseur.date.RecurrentDates.Cycle.WEEK;
import static com.codesseur.date.RecurrentDates.Cycle.WORKING_DAY;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;

import com.codesseur.date.HopCollector;
import com.codesseur.date.RecurrentDate;
import com.codesseur.date.RecurrentDates;
import com.codesseur.date.RecurrentDates.Cycle;
import com.codesseur.date.assertion.DateAssertions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CalendarRecurrentDateTest {

  private DateObjectMother dates = new DateObjectMother();

  @Test
  public void everyOneWorkingDayStartingNextSaturday() {
    LocalDateTime saturday = dates.next(SATURDAY);

    RecurrentDate date = RecurrentDates.from(saturday).every(1, WORKING_DAY).build();

    LocalDateTime monday = dates.nextAfter(MONDAY, saturday);
    DateAssertions.assertThat(date)
        .hasNextFuture(monday)
        .hasNext(dates.nextAfter(TUESDAY, saturday));
  }

  @Test
  public void everyOneWorkingDayStartingNextMonday() {
    LocalDateTime monday = dates.next(MONDAY);

    RecurrentDate date = RecurrentDates.from(monday).every(1, WORKING_DAY).build();

    DateAssertions.assertThat(date)
        .hasNextFuture(3, monday.plusDays(2))
        .hasNextFuture(monday)
        .hasNext(monday.plusDays(1));
  }

  @Test
  public void everyTwoWorkingDayStartingNextSaturday() {
    LocalDateTime saturday = dates.next(SATURDAY);

    RecurrentDate date = RecurrentDates.from(saturday).every(2, WORKING_DAY).build();

    LocalDateTime monday = dates.nextAfter(MONDAY, saturday);
    DateAssertions.assertThat(date)
        .hasNextFuture(monday)
        .hasNext(dates.nextAfter(WEDNESDAY, saturday));
  }

  @Test
  public void everyDayStartingTomorrow() {
    LocalDateTime tomorrow = dates.tomorrow();

    RecurrentDate date = RecurrentDates.from(tomorrow).every(1, DAY).build();

    DateAssertions.assertThat(date)
        .hasNextFuture(tomorrow)
        .hasNext(tomorrow.plusDays(1));
  }

  @Test
  public void everyTwoDaysStartingTomorrow() {
    LocalDateTime tomorrow = dates.tomorrow();

    RecurrentDate date = RecurrentDates.from(tomorrow).every(2, DAY).build();

    DateAssertions.assertThat(date)
        .hasNextFuture(tomorrow)
        .hasNext(tomorrow.plusDays(2));
  }

  @Test
  public void everyWeekStartingTomorrow() {
    LocalDateTime tomorrow = dates.tomorrow();

    RecurrentDate date = RecurrentDates.from(tomorrow).every(1, WEEK).build();

    DateAssertions.assertThat(date)
        .hasNextFuture(tomorrow)
        .hasNext(tomorrow.plusDays(7));
  }

  @Test
  public void everyTwoWeekStartingTomorrow() {
    LocalDateTime tomorrow = dates.tomorrow();

    RecurrentDate date = RecurrentDates.from(tomorrow).every(2, WEEK).build();

    DateAssertions.assertThat(date)
        .hasNextFuture(tomorrow)
        .hasNext(tomorrow.plusDays(14));
  }

  @Test
  public void everyMonthStartingTomorrow() {
    LocalDateTime tomorrow = dates.tomorrow();

    RecurrentDate date = RecurrentDates.from(tomorrow).every(1, MONTH).build();

    DateAssertions.assertThat(date)
        .hasNextFuture(tomorrow)
        .hasNext(tomorrow.plusMonths(1));
  }

  @Test
  public void everyTwoMonthsStartingTomorrow() {
    LocalDateTime tomorrow = dates.tomorrow();

    RecurrentDate date = RecurrentDates.from(tomorrow).every(2, MONTH).build();

    DateAssertions.assertThat(date)
        .hasNextFuture(tomorrow)
        .hasNext(tomorrow.plusMonths(2));
  }

  @Test
  public void everyLastDayOfMonthStartingTomorrow() {
    LocalDateTime tomorrow = dates.tomorrow();

    RecurrentDate date = RecurrentDates.from(tomorrow).every(1, MONTH).onTheLastDayOfMonth().build();

    LocalDateTime expectedNextFuture = dates.nextLastDayOfMonthAfter(tomorrow);
    DateAssertions.assertThat(date)
        .hasNextFuture(expectedNextFuture);
  }

  @Test
  public void next() {
    LocalDateTime date = date("2019-10-15T12:00:00");
    RecurrentDate recurrentDate = RecurrentDates.from(date).every(1, Cycle.MONTH).build();
    Optional<RecurrentDate> next = recurrentDate.next();

    LocalDateTime expected = date("2019-11-15T12:00");
    Assertions.assertThat(next)
        .hasValueSatisfying(
            v -> Assertions.assertThat(v.dateTime()).isEqualTo(expected));
  }

  @Test
  public void iterate() {
    LocalDateTime saturday = dates.last(SATURDAY);
    LocalDateTime nextSaturday = dates.next(FRIDAY);

    RecurrentDate date = RecurrentDates.from(saturday).every(1, WORKING_DAY).build();

    Integer reduce = date.stream(take(5).from(saturday).until(nextSaturday))
        .collect(HopCollector.of(0, v -> v + 1));

    Assertions.assertThat(reduce).isEqualTo(4);
  }

  private LocalDateTime date(String input) {
    DateTimeFormatter f = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    return LocalDateTime.parse(input, f);
  }
}