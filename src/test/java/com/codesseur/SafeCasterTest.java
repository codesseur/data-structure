package com.codesseur;

import com.codesseur.iterate.Streamed;
import com.codesseur.reflect.Type.$;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SafeCasterTest {

  @Test
  public void useSafeCastWithValidType() {
    Object name = "test";

    Optional<String> op = SafeCaster.safeCast(name, $.$());

    Assertions.assertThat(op).hasValue("test");
  }

  @Test
  public void safeCastWithInvalidType() {
    Object name = "test";

    Optional<Boolean> op = SafeCaster.safeCast(name, $.$());

    Assertions.assertThat(op).isEmpty();
  }

  @Test
  public void useSafeCastValidTypeWithFunction() {
    Object name = "test";

    Function<Object, Optional<String>> op = SafeCaster.safeCast($.$());

    Assertions.assertThat(op.apply(name)).hasValue("test");
  }

  @Test
  public void safeCastInvalidTypeWithFunction() {
    Object name = "test";

    Function<Object, Optional<Boolean>> op = SafeCaster.safeCast($.$());

    Assertions.assertThat(op.apply(name)).isEmpty();
  }

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

  @Test
  public void useSafeCastToStreamFunction() {
    Object name = "test";

    Stream<String> op = SafeCaster.safeCastToStream(String.class).apply(name);

    Assertions.assertThat(op).containsOnly("test");
  }

  @Test
  public void useSafeCastToStreamFunctionInvalid() {
    Object name = true;

    Stream<String> op = SafeCaster.safeCastToStream($.<String>$()).apply(name);

    Assertions.assertThat(op).isEmpty();
  }
}