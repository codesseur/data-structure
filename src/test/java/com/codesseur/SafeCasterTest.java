package com.codesseur;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SafeCasterTest {

  @Test
  public void useSafeCast() {
    Object name = "test";

    Optional<String> op = SafeCaster.safeCast(name, String.class);

    Assertions.assertThat(op).hasValue("test");
  }

  @Test
  public void useSafeCastInvalid() {
    Object name = true;

    Optional<String> op = SafeCaster.safeCast(name, String.class);

    Assertions.assertThat(op).isEmpty();
  }

  @Test
  public void useSafeCastFunction() {
    Object name = "test";

    Optional<String> op = SafeCaster.safeCast(String.class).apply(name);

    Assertions.assertThat(op).hasValue("test");
  }

  @Test
  public void useSafeCastFunctionInvalid() {
    Object name = true;

    Optional<String> op = SafeCaster.safeCast(String.class).apply(name);

    Assertions.assertThat(op).isEmpty();
  }
}