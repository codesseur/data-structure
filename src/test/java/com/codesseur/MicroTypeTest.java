package com.codesseur;

import io.vavr.Tuple2;
import java.util.Objects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MicroTypeTest {
  @Test
  public void hasMatchingValue() {
    MicroType<String> value = () -> "v";

    Assertions.assertThat(value.hasValue("v")).isTrue();
  }

  @Test
  public void hasNonMatchingValue() {
    MicroType<String> value = () -> "v";

    Assertions.assertThat(value.hasValue("v1")).isFalse();
  }

  @Test
  public void isEqualsMatchingValue() {
    StringValue v1 = new StringValue("v");
    StringValue v2 = new StringValue("v");

    Assertions.assertThat(v1.isEqual(v2)).isTrue();
    Assertions.assertThat(v1.isNotEqual(v2)).isFalse();
  }

  @Test
  public void isEqualsNonMatchingValue() {
    StringValue v1 = new StringValue("v1");
    StringValue v2 = new StringValue("v2");

    Assertions.assertThat(v1.isEqual(v2)).isFalse();
    Assertions.assertThat(v1.isNotEqual(v2)).isTrue();
  }

  @Test
  public void pairUp() {
    StringValue v1 = new StringValue("v1");
    Tuple2<String, String> v2 = v1.with("v2");

    Assertions.assertThat(v2._1).isEqualTo("v1");
    Assertions.assertThat(v2._2).isEqualTo("v2");
  }


  static class StringValue implements MicroType<String> {
    private final String value;

    StringValue(String value) {
      this.value = value;
    }

    public String value() {
      return value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      StringValue stringValue1 = (StringValue) o;
      return Objects.equals(value, stringValue1.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }
}