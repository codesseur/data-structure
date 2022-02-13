package com.codesseur.iterate.container;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SequenceTest {

  @Test
  void ofVarArgs() {
    Sequence<String> seq = Sequence.of("v1", "v2");

    Assertions.assertThat((Iterable<String>) seq).containsOnly("v1", "v2");
  }

  @Test
  void ofMultiple() {
    Sequence<String> seq = Sequence.of(Sequence.of("v1", "v2"), Sequence.of("v3", "v4"));

    Assertions.assertThat((Iterable<String>) seq).containsOnly("v1", "v2", "v3", "v4");
  }

  @Test
  void ofStream() {
    Sequence<String> seq = Sequence.of(Stream.of("v1", "v2"));

    Assertions.assertThat((Iterable<String>) seq).containsOnly("v1", "v2");
  }

  @Test
  void ofIterator() {
    Sequence<String> seq = Sequence.of(Arrays.asList("v1", "v2").listIterator());

    Assertions.assertThat((Iterable<String>) seq).containsOnly("v1", "v2");
  }

  @Test
  void ofSet() {
    Sequence<String> seq = Sequence.of(Collections.singleton("v1"));

    Assertions.assertThat((Iterable<String>) seq).containsOnly("v1");
  }

  @Test
  void ofOptionals() {
    Sequence<String> seq = Sequence.nonEmptyOf(Optional.of("v1"), Optional.empty());

    Assertions.assertThat((Iterable<String>) seq).containsOnly("v1");
  }

  @Test
  void ofNullables() {
    Sequence<String> seq = Sequence.nonNullOf("v1", null);

    Assertions.assertThat((Iterable<String>) seq).containsOnly("v1");
  }

  @Test
  void toOptional() {
    Sequence<String> seq = Sequence.of("v1");

    Assertions.assertThat(seq.toOptional()).hasValue(seq);
  }

  @Test
  void toOptionalWithEmptySequence() {
    Sequence<String> seq = Sequence.empty();

    Assertions.assertThat(seq.toOptional()).isEmpty();
  }

  @Test
  void repeatThreeTimes() {
    Sequence<String> seq = Sequence.repeat(i -> "v" + i, 3);

    Assertions.assertThat((Stream<String>) seq).containsOnly("v0", "v1", "v2");
  }

  @Test
  void repeat() {
    Sequence<Void> seq = Sequence.repeat(4);

    Assertions.assertThat((Stream<Void>) seq).hasSize(4);
  }

  @Test
  void trimBoth() {
    Sequence<String> seq = Sequence.of("v1", "w1", "v1");

    Assertions.assertThat((Stream<String>) seq.trim(s -> s.startsWith("v"))).containsOnly("w1");
  }

  @Test
  void trimLeft() {
    Sequence<String> seq = Sequence.of("v1", "w1", "v1");

    Assertions.assertThat((Stream<String>) seq.trimLeft(s -> s.startsWith("v"))).containsOnly("w1", "v1");
  }

  @Test
  void trimRight() {
    Sequence<String> seq = Sequence.of("v1", "w1", "v1");

    Assertions.assertThat((Stream<String>) seq.trimRight(s -> s.startsWith("v"))).containsOnly("v1", "w1");
  }

  @Test
  void getIndex() {
    Sequence<String> seq = Sequence.of("v1", "w1", "v1");

    Assertions.assertThat(seq.get(1)).hasValue("w1");
  }

  @Test
  void containsWithExisting() {
    Sequence<String> seq = Sequence.of("v1", "w1", "v1");

    Assertions.assertThat(seq.contains("v1")).isTrue();
  }

  @Test
  void containsWithAbsent() {
    Sequence<String> seq = Sequence.of("v1", "w1", "v1");

    Assertions.assertThat(seq.contains("t1")).isFalse();
  }

  @Test
  void notContainsWithExisting() {
    Sequence<String> seq = Sequence.of("v1", "w1", "v1");

    Assertions.assertThat(seq.notContains("v1")).isFalse();
  }

  @Test
  void notContainsWithAbsent() {
    Sequence<String> seq = Sequence.of("v1", "w1", "v1");

    Assertions.assertThat(seq.notContains("t1")).isTrue();
  }

  @Test
  void notContainsByWithExisting() {
    Sequence<String> seq = Sequence.of("v1", "w1", "v1");

    Assertions.assertThat(seq.notContainsBy(String::length, 2)).isFalse();
  }

  @Test
  void notContainsByWithAbsent() {
    Sequence<String> seq = Sequence.of("v1", "w1", "v1");

    Assertions.assertThat(seq.notContainsBy(String::length, 3)).isTrue();
  }

  @Test
  void containsByWithExisting() {
    Sequence<String> seq = Sequence.of("v1", "w1", "v1");

    Assertions.assertThat(seq.containsBy(String::length, 2)).isTrue();
  }

  @Test
  void containsByWithAbsent() {
    Sequence<String> seq = Sequence.of("v1", "w1", "v1");

    Assertions.assertThat(seq.containsBy(String::length, 3)).isFalse();
  }

  @Test
  void applyIfNotEmptyWithExisting() {
    Sequence<String> seq = Sequence.of("v1", "w1", "v1");

    Assertions.assertThat(seq.applyIfNotEmpty(List::size)).hasValue(3);
  }

  @Test
  void applyIfNotEmptyWithEmpty() {
    Sequence<String> seq = Sequence.empty();

    Assertions.assertThat(seq.applyIfNotEmpty(List::size)).isEmpty();
  }

}