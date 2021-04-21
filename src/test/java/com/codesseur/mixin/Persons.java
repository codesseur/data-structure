package com.codesseur.mixin;

import com.codesseur.mixin.iterate.container.Sequence;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Persons implements Sequence<String> {

  private final List<String> values;

  public Persons(String...values) {
    this(Arrays.asList(values));
  }

  public Persons(List<? extends String> values) {
    this.values = (List<String>) values;
  }

  @Override
  public List<String> value() {
    return values;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Persons persons = (Persons) o;
    return Objects.equals(values, persons.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(values);
  }
}
