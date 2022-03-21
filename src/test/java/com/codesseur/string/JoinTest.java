package com.codesseur.string;

import com.codesseur.iterate.Streamed;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class JoinTest {

  @Test
  public void standardJoinByLineOnNonEmpty() {
    String result = Streamed.of("a", "b").collect(Join.join().byLine());

    Assertions.assertThat(result).isEqualTo("a\nb");
  }

  @Test
  public void standardJoinByLineOnOneElement() {
    String result = Streamed.of("a").collect(Join.join().byLine());

    Assertions.assertThat(result).isEqualTo("a");
  }

  @Test
  public void standardJoinByLineOnEmpty() {
    String result = Streamed.<String>empty().collect(Join.join().byLine());

    Assertions.assertThat(result).isEqualTo("");
  }

  @Test
  public void standardJoinByTwoLinesOnNonEmpty() {
    String result = Streamed.of("a", "b").collect(Join.join().byLines(2));

    Assertions.assertThat(result).isEqualTo("a\n\nb");
  }

  @Test
  public void standardJoinByTwoLinesOnOneElement() {
    String result = Streamed.of("a").collect(Join.join().byLines(2));

    Assertions.assertThat(result).isEqualTo("a");
  }

  @Test
  public void standardJoinByTwoLinesOnEmpty() {
    String result = Streamed.<String>empty().collect(Join.join().byLines(2));

    Assertions.assertThat(result).isEqualTo("");
  }

  @Test
  public void standardJoinByLineWithPrefixSuffixOnNonEmpty() {
    String result = Streamed.of("a", "b").collect(Join.join().byLine("@", "#"));

    Assertions.assertThat(result).isEqualTo("@a\nb#");
  }

  @Test
  public void standardJoinByLineWithPrefixSuffixOnOneElement() {
    String result = Streamed.of("a").collect(Join.join().byLine("@", "#"));

    Assertions.assertThat(result).isEqualTo("@a#");
  }

  @Test
  public void standardJoinByLineWithPrefixSuffixOnEmpty() {
    String result = Streamed.<String>empty().collect(Join.join().byLine("@", "#"));

    Assertions.assertThat(result).isEqualTo("@#");
  }

  @Test
  public void standardJoinByTwoLinesWithPrefixSuffixOnNonEmpty() {
    String result = Streamed.of("a", "b").collect(Join.join().byLines(2, "@", "#"));

    Assertions.assertThat(result).isEqualTo("@a\n\nb#");
  }

  @Test
  public void standardJoinByTwoLinesWithPrefixSuffixOnOneElement() {
    String result = Streamed.of("a").collect(Join.join().byLines(2, "@", "#"));

    Assertions.assertThat(result).isEqualTo("@a#");
  }

  @Test
  public void standardJoinByTwoLinesWithPrefixSuffixOnEmpty() {
    String result = Streamed.<String>empty().collect(Join.join().byLines(2, "@", "#"));

    Assertions.assertThat(result).isEqualTo("@#");
  }

  @Test
  public void standardJoinByLineWithPrefixOnNonEmpty() {
    String result = Streamed.of("a", "b").collect(Join.join().byLinePrefixed("@"));

    Assertions.assertThat(result).isEqualTo("@a\nb");
  }

  @Test
  public void standardJoinByLineWithPrefixOnOneElement() {
    String result = Streamed.of("a").collect(Join.join().byLinePrefixed("@"));

    Assertions.assertThat(result).isEqualTo("@a");
  }

  @Test
  public void standardJoinByLineWithPrefixOnEmpty() {
    String result = Streamed.<String>empty().collect(Join.join().byLinePrefixed("@"));

    Assertions.assertThat(result).isEqualTo("@");
  }

  @Test
  public void standardJoinByLineWithSuffixOnNonEmpty() {
    String result = Streamed.of("a", "b").collect(Join.join().byLineSuffixed("#"));

    Assertions.assertThat(result).isEqualTo("a\nb#");
  }

  @Test
  public void standardJoinByLineWithSuffixOnOneElement() {
    String result = Streamed.of("a").collect(Join.join().byLineSuffixed("#"));

    Assertions.assertThat(result).isEqualTo("a#");
  }

  @Test
  public void standardJoinByLineWithSuffixOnEmpty() {
    String result = Streamed.<String>empty().collect(Join.join().byLineSuffixed("#"));

    Assertions.assertThat(result).isEqualTo("#");
  }

  @Test
  public void standardJoinDelimitedOnNonEmpty() {
    String result = Streamed.of("a", "b").collect(Join.join().delimited("#"));

    Assertions.assertThat(result).isEqualTo("a#b");
  }

  @Test
  public void standardJoinDelimitedOnOneElement() {
    String result = Streamed.of("a").collect(Join.join().delimited("#"));

    Assertions.assertThat(result).isEqualTo("a");
  }

  @Test
  public void standardJoinDelimitedOnEmpty() {
    String result = Streamed.<String>empty().collect(Join.join().delimited("#"));

    Assertions.assertThat(result).isEqualTo("");
  }

  @Test
  public void standardJoinDelimitedPrefixedOnNonEmpty() {
    String result = Streamed.of("a", "b").collect(Join.join().delimitedPrefixed("#", ";"));

    Assertions.assertThat(result).isEqualTo(";a#b");
  }

  @Test
  public void standardJoinDelimitedPrefixedOnOneElement() {
    String result = Streamed.of("a").collect(Join.join().delimitedPrefixed("#", ";"));

    Assertions.assertThat(result).isEqualTo(";a");
  }

  @Test
  public void standardJoinDelimitedPrefixedOnEmpty() {
    String result = Streamed.<String>empty().collect(Join.join().delimitedPrefixed("#", ";"));

    Assertions.assertThat(result).isEqualTo(";");
  }

  @Test
  public void standardJoinDelimitedSuffixedOnNonEmpty() {
    String result = Streamed.of("a", "b").collect(Join.join().delimitedSuffixed("#", ";"));

    Assertions.assertThat(result).isEqualTo("a#b;");
  }

  @Test
  public void standardJoinDelimitedSuffixedOnOneElement() {
    String result = Streamed.of("a").collect(Join.join().delimitedSuffixed("#", ";"));

    Assertions.assertThat(result).isEqualTo("a;");
  }

  @Test
  public void standardJoinDelimitedSuffixedOnEmpty() {
    String result = Streamed.<String>empty().collect(Join.join().delimitedSuffixed("#", ";"));

    Assertions.assertThat(result).isEqualTo(";");
  }



  @Test
  public void ignoreIfEmptyJoinDelimitedSuffixedOnNonEmpty() {
    String result = Streamed.of("a", "b").collect(Join.joinIgnoringPrefixAndSuffixIfEmpty().byLine("@", "#"));

    Assertions.assertThat(result).isEqualTo("@a\nb#");
  }


  @Test
  public void ignoreIfEmptyJoinDelimitedSuffixedOnEmpty() {
    String result = Streamed.<String>empty().collect(Join.joinIgnoringPrefixAndSuffixIfEmpty().byLine("@", "#"));

    Assertions.assertThat(result).isEqualTo("");
  }
}
