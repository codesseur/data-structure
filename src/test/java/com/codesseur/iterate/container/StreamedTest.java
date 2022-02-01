package com.codesseur.iterate.container;

import com.codesseur.iterate.Streamed;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class StreamedTest {

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
  void appendStreamdOfElements() {
    Streamed<String> streamed = Streamed.of("v1", "v2");

    Assertions.assertThat((Iterable<String>) streamed.append(Streamed.of("v3"))).containsOnly("v1", "v2", "v3");
  }

}
