package com.codesseur.iterate.container;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BagTest {

  @Test
  void ofVarArgs() {
    Bag<String> bag = Bag.of("v1", "v2");

    Assertions.assertThat((Iterable<String>) bag).containsOnly("v1", "v2");
  }

  @Test
  void ofMultiple() {
    Bag<String> bag = Bag.of(Bag.of("v1", "v2"), Bag.of("v3", "v4"));

    Assertions.assertThat((Iterable<String>) bag).containsOnly("v1", "v2", "v3", "v4");
  }

  @Test
  void ofStream() {
    Bag<String> bag = Bag.of(Stream.of("v1", "v2"));

    Assertions.assertThat((Iterable<String>) bag).containsOnly("v1", "v2");
  }

  @Test
  void ofIterator() {
    Bag<String> bag = Bag.of(Arrays.asList("v1", "v2").listIterator());

    Assertions.assertThat((Iterable<String>) bag).containsOnly("v1", "v2");
  }

  @Test
  void ofSet() {
    Bag<String> bag = Bag.of(Collections.singleton("v1"));

    Assertions.assertThat((Iterable<String>) bag).containsOnly("v1");
  }

  @Test
  void ofOptionals() {
    Bag<String> bag = Bag.nonEmptyOf(Optional.of("v1"), Optional.empty());

    Assertions.assertThat((Iterable<String>) bag).containsOnly("v1");
  }

  @Test
  void ofNullables() {
    Bag<String> bag = Bag.nonNullOf("v1", null);

    Assertions.assertThat((Iterable<String>) bag).containsOnly("v1");
  }

  @Test
  void toOptional() {
    Bag<String> bag = Bag.of("v1");

    Assertions.assertThat(bag.toOptional()).hasValue(bag);
  }

  @Test
  void toOptionalWithEmptyBag() {
    Bag<String> bag = Bag.empty();

    Assertions.assertThat(bag.toOptional()).isEmpty();
  }

}