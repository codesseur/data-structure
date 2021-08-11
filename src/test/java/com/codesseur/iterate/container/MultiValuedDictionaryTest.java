package com.codesseur.iterate.container;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MultiValuedDictionaryTest {

  @Test
  void empty() {
    MultiValuedDictionary<String, String> map = MultiValuedDictionary.empty();

    Assertions.assertThat(map.value()).isEmpty();
  }

  @Test
  void ofTuples() {
    MultiValuedDictionary<String, String> map = MultiValuedDictionary.of(Tuple.of("k1", "v1"), Tuple.of("k1", "v2"), Tuple.of("k2", "v2"));

    Assertions.assertThat(map.value())
        .hasSize(2)
        .containsEntry("k1", Sequence.of("v1", "v2"))
        .containsEntry("k2", Sequence.of("v2"));
  }

  @Test
  void ofMap() {
    Map<String, List<String>> value = Map.of("k1", List.of("v1", "v2"), "k2", List.of("v2"));
    MultiValuedDictionary<String, String> map = MultiValuedDictionary.of(value);

    Assertions.assertThat(map.value())
        .hasSize(2)
        .containsEntry("k1", Sequence.of("v1", "v2"))
        .containsEntry("k2", Sequence.of("v2"));
  }

  @Test
  void ofStream() {
    Stream<Tuple2<String, String>> value = Stream.of(Tuple.of("k1", "v1"), Tuple.of("k1", "v2"), Tuple.of("k2", "v2"));

    MultiValuedDictionary<String, String> map = MultiValuedDictionary.of(value);

    Assertions.assertThat(map.value())
        .hasSize(2)
        .containsEntry("k1", Sequence.of("v1", "v2"))
        .containsEntry("k2", Sequence.of("v2"));
  }

  @Test
  void getSingle() {
    MultiValuedDictionary<String, String> map = MultiValuedDictionary.of(Tuple.of("k1", "v1"), Tuple.of("k1", "v2"), Tuple.of("k2", "v2"));

    Assertions.assertThat(map.getSingle("k1")).hasValue("v1");
  }

  @Test
  void getMulti() {
    MultiValuedDictionary<String, String> map = MultiValuedDictionary.of(Tuple.of("k1", "v1"), Tuple.of("k1", "v2"), Tuple.of("k2", "v2"));

    Assertions.assertThat((Stream<String>) map.getMulti("k1")).containsExactly("v1", "v2");
  }
}