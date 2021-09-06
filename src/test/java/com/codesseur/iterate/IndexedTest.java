package com.codesseur.iterate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class IndexedTest {

  @Test
  public void map() {
    Indexed<String> value = Indexed.last(0, "value");

    Indexed<Integer> mapped = value.map(v -> 1);
    Assertions.assertThat(mapped.index()).isEqualTo(0);
    Assertions.assertThat(mapped.value()).isEqualTo(1);
  }
}