package com.codesseur.iterate;

import com.codesseur.iterate.container.Sequence;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
}
