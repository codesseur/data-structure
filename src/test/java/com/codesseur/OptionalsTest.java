package com.codesseur;

import com.codesseur.Optionals.SafeOptionalCombiner.UnmatchedCombination;
import io.vavr.Tuple2;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OptionalsTest {

  @Test
  public void streamOnEmpty() {
    Stream<Object> value = Optionals.stream(Optional.empty());

    Assertions.assertThat(value).isEmpty();
  }

  @Test
  public void streamOnValue() {
    Stream<String> value = Optionals.stream(Optional.of("v"));

    Assertions.assertThat(value).containsOnly("v");
  }

  @Test
  public void streamOnOptionals() {
    Stream<Object> value = Optionals.stream(Optional::empty, () -> Optional.of("v"));

    Assertions.assertThat(value).containsOnly("v");
  }

  @Test
  public void orOnOptionals() {
    Optional<String> value = Optionals.or(Optional.empty(), Optional.of("v1"), Optional.of("v2"));

    Assertions.assertThat(value).hasValue("v1");
  }

  @Test
  public void orOnSuppliers() {
    Optional<String> value = Optionals.or(Optional::empty, () -> Optional.of("v1"), () -> Optional.of("v2"));

    Assertions.assertThat(value).hasValue("v1");
  }

  @Test
  public void orOnNullables() {
    Optional<String> value = Optionals.or(null, "v1", "v2");

    Assertions.assertThat(value).hasValue("v1");
  }

  @Test
  public void orOnList() {
    Optional<String> value = Optionals
        .or(Arrays.asList(Optional::empty, () -> Optional.of("v1"), () -> Optional.of("v2")));

    Assertions.assertThat(value).hasValue("v1");
  }

  @Test
  public void orOnStream() {
    Optional<String> value = Optionals.or(Stream.of(Optional::empty, () -> Optional.of("v1"), () -> Optional.of("v2")));

    Assertions.assertThat(value).hasValue("v1");
  }

  @Test
  public void andStreamWithBothPresent() {
    Tuple2<String, String> value = Optionals.mandatoryAnd(Optional.of("v1"), Optional.of("v2"));

    Assertions.assertThat(value._1()).isEqualTo("v1");
    Assertions.assertThat(value._2()).isEqualTo("v2");
  }

  @Test
  public void andStreamWithFirstEmpty() {
    Assertions.assertThatThrownBy(() -> Optionals.mandatoryAnd(Optional.empty(), Optional.of("v1")))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void combineLeftOnBothPresent() {
    Optional<Integer> value = Optionals.join(Optional.of("v1"), Optional.of("v2")).combineLeft(String::length);

    Assertions.assertThat(value).isEmpty();
  }

  @Test
  public void combineSafeLeftOnBothPresent() {
    Supplier<Integer> v = () -> Optionals.join(Optional.of("v1"), Optional.of("v2")).uncheck()
        .combineLeft(String::length);

    Assertions.assertThatThrownBy(v::get).isInstanceOf(UnmatchedCombination.class);
  }

  @Test
  public void combineSafeLeftOnLeftOnly() {
    Integer v = Optionals.join(Optional.of("v1"), Optional.empty()).uncheck().combineLeft(String::length);

    Assertions.assertThat(v).isEqualTo(2);
  }

  @Test
  public void combineLeftOnRightPresent() {
    Optional<Integer> value = Optionals.join(Optional.<String>empty(), Optional.of("v2")).combineLeft(String::length);

    Assertions.assertThat(value).isEmpty();
  }

  @Test
  public void combineLeftOnLeftPresent() {
    Optional<Integer> value = Optionals.join(Optional.of("v1"), Optional.empty()).combineLeft(String::length);

    Assertions.assertThat(value).hasValue(2);
  }

  @Test
  public void combineRightOnBothPresent() {
    Optional<Integer> value = Optionals.join(Optional.of("v1"), Optional.of("v2")).combineRight(String::length);

    Assertions.assertThat(value).isEmpty();
  }

  @Test
  public void combineRightOnRightPresent() {
    Optional<Integer> value = Optionals.join(Optional.<String>empty(), Optional.of("v2")).combineRight(String::length);

    Assertions.assertThat(value).hasValue(2);
  }

  @Test
  public void combineRightOnLeftPresent() {
    Optional<Integer> value = Optionals.join(Optional.of("v1"), Optional.<String>empty()).combineRight(String::length);

    Assertions.assertThat(value).isEmpty();
  }

  @Test
  public void combineAllOnBothPresent() {
    String value = Optionals.join(Optional.of("v1"), Optional.of("v2")).combineAll(String::concat);

    Assertions.assertThat(value).isEqualTo("v1v2");
  }

  @Test
  public void uniJoinOnLeftPresent() {
    Optional<Integer> value = Optionals.join(Optional.of("v1"), Optional.<String>empty()).sameType()
        .combine((i1, i2) -> 3, String::length);

    Assertions.assertThat(value).hasValue(2);
  }

}