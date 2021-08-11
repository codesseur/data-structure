package com.codesseur.functions;

import java.util.function.BiFunction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ProjectionsTest {

  @Test
  void first() {
    BiFunction<String,Boolean,String> f = Projections.first();

    Assertions.assertThat(f.apply("v", false)).isEqualTo("v");
  }

  @Test
  void second() {
    BiFunction<String,Boolean,Boolean> f = Projections.second();

    Assertions.assertThat(f.apply("v", false)).isFalse();
  }
}