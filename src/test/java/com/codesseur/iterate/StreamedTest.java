package com.codesseur.iterate;

import com.codesseur.iterate.container.Sequence;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class StreamedTest {

  @Test
  void toOptional() {
    Optional<Sequence<String>> streamed = Streamed.of("v1", "v2").toOptional(Streamed::toSequence);

    Assertions.assertThat(streamed).hasValue(Sequence.of("v1", "v2"));
  }

  @Test
  void appendOneElement() {
    Streamed<String> streamed = Streamed.of("v1", "v2");

    Assertions.assertThat((Iterable<String>) streamed.append("v3")).containsOnly("v1", "v2", "v3");
  }

  @Test
  void appendTwoElements() {
    Streamed<String> streamed = Streamed.of("v1", "v2");

    Assertions.assertThat((Iterable<String>) streamed.append("v3", "v4")).containsOnly("v1", "v2", "v3", "v4");
  }

  @Test
  void appendStreamOfElements() {
    Streamed<String> streamed = Streamed.of("v1", "v2");

    Assertions.assertThat((Iterable<String>) streamed.append(Stream.of("v3"))).containsOnly("v1", "v2", "v3");
  }

  @Test
  void appendStreamedOfElements() {
    Streamed<String> streamed = Streamed.of("v1", "v2");

    Assertions.assertThat((Iterable<String>) streamed.append(Streamed.of("v3"))).containsOnly("v1", "v2", "v3");
  }

  @Test
  void prependOneElement() {
    Streamed<String> streamed = Streamed.of("v1", "v2");

    Assertions.assertThat((Iterable<String>) streamed.prepend("v3")).containsOnly("v3", "v1", "v2");
  }

  @Test
  void prependTwoElements() {
    Streamed<String> streamed = Streamed.of("v1", "v2");

    Assertions.assertThat((Iterable<String>) streamed.prepend("v3", "v4")).containsOnly("v3", "v4", "v1", "v2");
  }

  @Test
  void prependStreamOfElements() {
    Streamed<String> streamed = Streamed.of("v1", "v2");

    Assertions.assertThat((Iterable<String>) streamed.prepend(Stream.of("v3"))).containsOnly("v3", "v1", "v2");
  }

  @Test
  void prependStreamedOfElements() {
    Streamed<String> streamed = Streamed.of("v1", "v2");

    Assertions.assertThat((Iterable<String>) streamed.prepend(Streamed.of("v3"))).containsOnly("v3", "v1", "v2");
  }

  @Test
  void headAndTailWithTwoElements() {
    Tuple2<Optional<String>, Streamed<String>> headAndTail = Streamed.of("v1", "v2").headAndTail();

    Assertions.assertThat(headAndTail._1()).hasValue("v1");
    Assertions.assertThat((Iterable<String>) headAndTail._2()).containsExactly("v2");
  }

  @Test
  void headAndTailWithoutElements() {
    Tuple2<Optional<String>, Streamed<String>> headAndTail = Streamed.<String>empty().headAndTail();

    Assertions.assertThat(headAndTail._1()).isEmpty();
    Assertions.assertThat((Iterable<String>) headAndTail._2()).isEmpty();
  }

  @Test
  void headAndTailWithOneElements() {
    Tuple2<Optional<String>, Streamed<String>> headAndTail = Streamed.of("v1").headAndTail();

    Assertions.assertThat(headAndTail._1()).hasValue("v1");
    Assertions.assertThat((Iterable<String>) headAndTail._2()).isEmpty();
  }

  @Test
  void headAndTailWithThreeElements() {
    Tuple2<Optional<String>, Streamed<String>> headAndTail = Streamed.of("v1", "v2", "v3").headAndTail();

    Assertions.assertThat(headAndTail._1()).hasValue("v1");
    Assertions.assertThat((Iterable<String>) headAndTail._2()).containsExactly("v2", "v3");
  }

  @Test
  void leadAndLastWithTwoElements() {
    Tuple2<Streamed<String>, Optional<String>> lastAndLead = Streamed.of("v1", "v2").lastAndLead();

    Assertions.assertThat((Iterable<String>) lastAndLead._1()).containsExactly("v1");
    Assertions.assertThat(lastAndLead._2()).hasValue("v2");
  }

  @Test
  void leadAndLastWithoutElements() {
    Tuple2<Streamed<String>, Optional<String>> lastAndLead = Streamed.<String>empty().lastAndLead();

    Assertions.assertThat((Iterable<String>) lastAndLead._1()).isEmpty();
    Assertions.assertThat(lastAndLead._2()).isEmpty();
  }

  @Test
  void leadAndLastWithOneElements() {
    Tuple2<Streamed<String>, Optional<String>> lastAndLead = Streamed.of("v1").lastAndLead();

    Assertions.assertThat((Iterable<String>) lastAndLead._1()).isEmpty();
    Assertions.assertThat(lastAndLead._2()).hasValue("v1");
  }

  @Test
  void leadAndLastWithThreeElements() {
    Tuple2<Streamed<String>, Optional<String>> lastAndLead = Streamed.of("v1", "v2", "v3").lastAndLead();

    Assertions.assertThat((Iterable<String>) lastAndLead._1()).containsExactly("v1", "v2");
    Assertions.assertThat(lastAndLead._2()).hasValue("v3");
  }

  @Test
  void peekIfWithThreeElements() {
    List<String> values = new ArrayList<>();
    Streamed.of("v1", "v2", "v3")
        .peekIf("v2"::equals, values::add)
        .toList();

    Assertions.assertThat(values).containsExactly("v2");
  }

  @Test
  void peekIfWithoutMatchingElements() {
    List<String> values = new ArrayList<>();
    Streamed.of("v1")
        .peekIf("v2"::equals, values::add)
        .toList();

    Assertions.assertThat(values).isEmpty();
  }

  @Test
  void peekIfWithoutElements() {
    List<String> values = new ArrayList<>();
    Streamed.<String>empty()
        .peekIf("v2"::equals, values::add)
        .toList();

    Assertions.assertThat(values).isEmpty();
  }

  @Test
  void mapStickyWithOneElement() {
    List<Tuple2<String, Boolean>> values = Streamed.of("v1").mapSticky("v2"::equals).toList();

    Assertions.assertThat(values).containsExactly(Tuple.of("v1", false));
  }

  @Test
  void flatMapStickyWithOneElement() {
    List<Tuple2<String, String>> values = Streamed.of("v1").flatMapSticky(v -> Streamed.of(v.split(""))).toList();

    Assertions.assertThat(values).containsExactly(Tuple.of("v1", "v"), Tuple.of("v1", "1"));
  }

  @Test
  void ifNotEmptyWithOneElement() {
    Optional<Long> aLong = Streamed.of("v1").ifNotEmpty(Streamed::count);

    Assertions.assertThat(aLong).hasValue(1L);
  }

  @Test
  void ifNotEmptyWithoutElement() {
    Optional<Long> aLong = Streamed.empty().ifNotEmpty(Streamed::count);

    Assertions.assertThat(aLong).isEmpty();
  }

  @Test
  void toListThenWithTwoElements() {
    int size = Streamed.of("v1", "v2").toListThen(List::size);

    Assertions.assertThat(size).isEqualTo(2);
  }

  @Test
  void toSetThenWithTwoElements() {
    int size = Streamed.of("v1", "v2").toSetThen(Set::size);

    Assertions.assertThat(size).isEqualTo(2);
  }

  @Test
  void toMapWithTwoElements() {
    Map<String, Integer> map = Streamed.of("v1", "v2").toMap(Function.identity(), String::length);

    Assertions.assertThat(map).hasSize(2).containsEntry("v1", 2).containsEntry("v2", 2);
  }

  @Test
  void toMapFromKeyWithTwoElements() {
    Map<String, Integer> map = Streamed.of("v1", "v2").toMap2(String::length);

    Assertions.assertThat(map).hasSize(2).containsEntry("v1", 2).containsEntry("v2", 2);
  }

  @Test
  void toMapFromValueWithTwoElements() {
    Map<String, String> map = Streamed.of("v1", "v2").toMap(Function.identity());

    Assertions.assertThat(map).hasSize(2).containsEntry("v1", "v1").containsEntry("v2", "v2");
  }

  @Test
  void toDictionaryWithTwoElements() {
    Map<String, Integer> map = Streamed.of("v1", "v2").toDictionary(Function.identity(), String::length).value();

    Assertions.assertThat(map).hasSize(2).containsEntry("v1", 2).containsEntry("v2", 2);
  }

  @Test
  void toDictionaryFromKeyWithTwoElements() {
    Map<String, Integer> map = Streamed.of("v1", "v2").toDictionary2(String::length).value();

    Assertions.assertThat(map).hasSize(2).containsEntry("v1", 2).containsEntry("v2", 2);
  }

  @Test
  void toDictionaryFromValueWithTwoElements() {
    Map<String, String> map = Streamed.of("v1", "v2").toDictionary(Function.identity()).value();

    Assertions.assertThat(map).hasSize(2).containsEntry("v1", "v1").containsEntry("v2", "v2");
  }

  @Test
  void ignoreUntilWithTwoElements() {
    final List<String> values = Streamed.of("v1", "v2").ignoreUntil("v2"::equals).toList();

    Assertions.assertThat(values).containsExactly("v2");
  }

  @Test
  void replaceIfOrWithMatchingElement() {
    List<String> values = Streamed.of("v1", "v2").replaceIf("v2"::equals, i -> "u" + i).toList();

    Assertions.assertThat(values).containsExactly("v1", "uv2");
  }

  @Test
  void replaceIfOrWithNoMatchingElement() {
    final List<String> values = Streamed.of("v1", "v2").replaceIf("w2"::equals, i -> "u" + i).toList();

    Assertions.assertThat(values).containsExactly("v1", "v2");
  }

  @Test
  void innerZipWithLongerRight() {
    List<Tuple2<String, String>> values = Streamed.of("v1", "v2").innerZip(Streamed.of("w1", "w2", "w3")).toList();

    Assertions.assertThat(values).containsExactly(Tuple.of("v1", "w1"), Tuple.of("v2", "w2"));
  }

  @Test
  void innerZipWithLongerLeft() {
    List<Tuple2<String, String>> values = Streamed.of("v1", "v2", "v3").innerZip(Streamed.of("w1", "w2")).toList();

    Assertions.assertThat(values).containsExactly(Tuple.of("v1", "w1"), Tuple.of("v2", "w2"));
  }
}
