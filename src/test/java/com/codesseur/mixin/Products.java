package com.codesseur.mixin;

import com.codesseur.mixin.iterate.container.Bag;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Products implements Bag<String> {

  private final Set<String> values;

  public Products(String...values) {
    this(new HashSet<>(Arrays.asList(values)));
  }

  public Products(Set<? extends String> values) {
    this.values = (Set<String>) values;
  }

  @Override
  public Set<String> value() {
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
    Products persons = (Products) o;
    return Objects.equals(values, persons.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(values);
  }
}
