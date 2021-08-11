package com.codesseur.iterate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class IndexedValueTest {

  @Test
  public void map() {
    IndexedValue<String> value = IndexedValue.last(0, "value");

    IndexedValue<Integer> mapped = value.map(v -> 1);
    Assertions.assertThat(mapped.index()).isEqualTo(0);
    Assertions.assertThat(mapped.value()).isEqualTo(1);
  }
}