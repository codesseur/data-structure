package com.codesseur.date;

import com.codesseur.iterate.Indexed;
import com.codesseur.iterate.Streamed;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.util.Optional;
import java.util.function.Predicate;

public class RecurrentDate {

  private final LocalDateTime origin;
  private final DateOperation dateOperation;
  private final TemporalAdjuster adjuster;
  private final TemporalAdjuster sanitizer;

  private RecurrentDate(LocalDateTime origin, DateOperation dateOperation, TemporalAdjuster adjuster,
      TemporalAdjuster sanitizer) {
    this.sanitizer = sanitizer;
    this.adjuster = adjuster;
    LocalDateTime otherOrigin = origin.with(this.sanitizer);
    this.origin = origin.equals(otherOrigin) ? origin : otherOrigin;
    this.dateOperation = dateOperation;
  }

  public LocalDateTime dateTime() {
    return origin.with(adjuster);
  }

  public Optional<RecurrentDate> next() {
    return dateOperation.apply(origin).map(this::with);
  }

  private RecurrentDate with(LocalDateTime dateTime) {
    return new Builder().from(this).origin(dateTime).build();
  }

  public Optional<RecurrentDate> next(LocalDateTime from) {
    return stream(Criteria.take(1).from(from)).findFirst();
  }

  public Streamed<RecurrentDate> stream(Criteria criteria) {
    return Streamed.iterate(this, criteria.until, d -> d.value().next()).filter(criteria.from);
  }

  public static class Criteria {

    private Predicate<RecurrentDate> from = r -> true;
    private Predicate<Indexed<RecurrentDate>> until;

    private Criteria(Predicate<Indexed<RecurrentDate>> limit) {
      this.until = limit;
    }

    public static Criteria take(int size) {
      return new Criteria(i -> i.index() < size);
    }

    public static Criteria takeAny() {
      return new Criteria(i -> true);
    }

    public Criteria until(LocalDateTime max) {
      until = until.and(r -> {
        LocalDateTime dateTime = r.value().dateTime();
        return dateTime.isBefore(max) || dateTime.equals(max);
      });
      return this;
    }

    public Criteria from(LocalDateTime start) {
      from = r -> r.dateTime().isAfter(start) || r.dateTime().equals(start);
      return this;
    }

  }

  public static class Builder {

    private LocalDateTime origin;
    private DateOperation dateOperation = d -> Optional.empty();
    private TemporalAdjuster adjuster = d -> d;
    private TemporalAdjuster sanitizer = d -> d;

    public Builder from(RecurrentDate value) {
      return origin(value.origin).dateOperation(value.dateOperation).adjuster(value.adjuster)
          .sanitizer(value.sanitizer);
    }

    public Builder origin(LocalDateTime origin) {
      this.origin = origin;
      return this;
    }

    public Builder dateOperation(DateOperation dateOperation) {
      this.dateOperation = dateOperation;
      return this;
    }

    public Builder adjuster(TemporalAdjuster adjuster) {
      this.adjuster = adjuster;
      return this;
    }

    public Builder sanitizer(TemporalAdjuster sanitizer) {
      this.sanitizer = sanitizer;
      return this;
    }

    public RecurrentDate build() {
      return new RecurrentDate(origin, dateOperation, adjuster, sanitizer);
    }
  }
}
