package com.codesseur.string;

import com.codesseur.iterate.container.Dictionary;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringsTest {

  @Test
  public void leftPadWithEmpty() {
    String result = Strings.leftPad("", 10);

    Assertions.assertThat(result).isEqualTo("          ");
  }

  @Test
  public void leftPadWithNull() {
    String result = Strings.leftPad(null, 10);

    Assertions.assertThat(result).isEqualTo("          ");
  }

  @Test
  public void leftPadWithNonNull() {
    String result = Strings.leftPad("b", 10);

    Assertions.assertThat(result).isEqualTo("         b");
  }

  @Test
  public void leftPadWithLargeNonNull() {
    String result = Strings.leftPad("bbb", 2);

    Assertions.assertThat(result).isEqualTo("bbb");
  }

  @Test
  public void rightPadWithEmpty() {
    String result = Strings.rightPad("", 10);

    Assertions.assertThat(result).isEqualTo("          ");
  }

  @Test
  public void rightPadWithNull() {
    String result = Strings.rightPad(null, 10);

    Assertions.assertThat(result).isEqualTo("          ");
  }

  @Test
  public void rightPadWithNonNull() {
    String result = Strings.rightPad("b", 10);

    Assertions.assertThat(result).isEqualTo("b         ");
  }

  @Test
  public void rightPadMultilineWithOneLargeLine() {
    String result = Strings.rightPadMultiline("bbb", 2);

    Assertions.assertThat(result).isEqualTo("bbb");
  }

  @Test
  public void rightPadMultilineWithOneLargeLineAndOneSmallLine() {
    String result = Strings.rightPadMultiline("bbb\nb", 2);

    Assertions.assertThat(result).isEqualTo("bbb\nb ");
  }

  @Test
  public void leftPadMultilineWithOneLargeLine() {
    String result = Strings.leftPadMultiline("bbb", 2);

    Assertions.assertThat(result).isEqualTo("bbb");
  }

  @Test
  public void leftPadMultilineWithOneLargeLineAndOneSmallLine() {
    String result = Strings.leftPadMultiline("bbb\nb", 2);

    Assertions.assertThat(result).isEqualTo("bbb\n b");
  }

  @Test
  public void notBlankOnNull() {
    Assertions.assertThat(Strings.notBlank(null)).isFalse();
  }

  @Test
  public void notBlankOnEmpty() {
    Assertions.assertThat(Strings.notBlank("")).isFalse();
  }

  @Test
  public void notBlankOnBlank() {
    Assertions.assertThat(Strings.notBlank(" ")).isFalse();
  }

  @Test
  public void notBlankOnNonEmpty() {
    Assertions.assertThat(Strings.notBlank("bbb")).isTrue();
  }

  @Test
  public void notEmptyOnNull() {
    Assertions.assertThat(Strings.notEmpty(null)).isFalse();
  }

  @Test
  public void notEmptyOnEmpty() {
    Assertions.assertThat(Strings.notEmpty("")).isFalse();
  }

  @Test
  public void notEmptyOnNonEmpty() {
    Assertions.assertThat(Strings.notEmpty("bbb")).isTrue();
  }

  @Test
  public void rightPadWithLargeNonNull() {
    String result = Strings.rightPad("bbb", 2);

    Assertions.assertThat(result).isEqualTo("bbb");
  }

  @Test
  public void linesWithNullText() {
    Stream<String> actual = Strings.lines(null);

    Assertions.assertThat(actual).isEmpty();
  }

  @Test
  public void linesWithEmpty() {
    Stream<String> actual = Strings.lines("");

    Assertions.assertThat(actual).containsExactly("");
  }

  @Test
  public void linesWithOneLine() {
    Stream<String> actual = Strings.lines("value");

    Assertions.assertThat(actual).containsExactly("value");
  }

  @Test
  public void linesWithOneLineAndLineBreak() {
    Stream<String> actual = Strings.lines("value\n");

    Assertions.assertThat(actual).containsExactly("value", "");
  }

  @Test
  public void linesWithTwoLine() {
    Stream<String> actual = Strings.lines("value\nvalue");

    Assertions.assertThat(actual).containsExactly("value", "value");
  }

  @Test
  public void betweenIfNotEmptyWithNullText() {
    String actual = Strings.betweenIfNotEmpty(null, "prefix", "suffix");

    Assertions.assertThat(actual).isNull();
  }

  @Test
  public void betweenIfNotEmptyWithEmptyText() {
    String actual = Strings.betweenIfNotEmpty("", "prefix", "suffix");

    Assertions.assertThat(actual).isEmpty();
  }

  @Test
  public void betweenIfNotEmptyWithNonEmptyText() {
    String actual = Strings.betweenIfNotEmpty("value", "prefix_", "_suffix");

    Assertions.assertThat(actual).isEqualTo("prefix_value_suffix");
  }

  @Test
  public void matchNull() {
    boolean matched = Strings.match("").on(null).isFullyMatched();

    Assertions.assertThat(matched).isTrue();
  }

  @Test
  public void matchEmpty() {
    boolean matched = Strings.match("").on("").isFullyMatched();

    Assertions.assertThat(matched).isTrue();
  }

  @Test
  public void matchMatched() {
    boolean matched = Strings.match("\\d+").on("123").isFullyMatched();

    Assertions.assertThat(matched).isTrue();
  }

  @Test
  public void matchUnmatched() {
    boolean matched = Strings.match("\\d+").on("123b").isFullyMatched();

    Assertions.assertThat(matched).isFalse();
  }

  @Test
  public void replaceOnMatched() {
    String result = Strings.match("\\d+")
        .on("123")
        .replace(m -> "1");

    Assertions.assertThat(result).isEqualTo("1");
  }

  @Test
  public void replaceOnPartialMatched() {
    String result = Strings.match("\\d").on("123").replace(m -> "1");

    Assertions.assertThat(result).isEqualTo("111");
  }

  @Test
  public void replaceOnUnmatched() {
    String result = Strings.match("\\d+")
        .on("abc")
        .replace(m -> "1");

    Assertions.assertThat(result).isEqualTo("abc");
  }

  @Test
  public void getOnUnmatched() {
    Optional<Integer> matched = Strings.match("\\d+")
        .on("abc")
        .mapFirst(i -> 1);

    Assertions.assertThat(matched).isEmpty();
  }

  @Test
  public void getOnMatched() {
    Optional<Integer> matched = Strings.match(".+")
        .on("abc")
        .mapFirst(i -> 1);

    Assertions.assertThat(matched).hasValue(1);
  }

  @Test
  public void getNullOnMatched() {
    Optional<Integer> matched = Strings.match(".+")
        .on("abc")
        .mapFirst(i -> null);

    Assertions.assertThat(matched).isEmpty();
  }

  @Test
  public void mapOnMatched() {
    Stream<Integer> matched = Strings.match(".")
        .on("abc")
        .map(m -> 1);

    Assertions.assertThat(matched).containsExactly(1, 1, 1);
  }

  @Test
  public void mapOnUnmatched() {
    Stream<Integer> matched = Strings.match("\\d")
        .on("abc")
        .map(m -> 1);

    Assertions.assertThat(matched).isEmpty();
  }

  @Test
  public void replaceRecursivelyOnUnmatched() {
    String result = Strings.match("(\\d)")
        .on("abc")
        .replaceRecursive(m -> "1");

    Assertions.assertThat(result).isEqualTo("abc");
  }

  @Test
  public void replaceRecursivelyOnMatched() {
    String result = Strings.match("(\\d{2})")
        .on("123")
        .replaceRecursive(m -> "1");

    Assertions.assertThat(result).isEqualTo("1");
  }

  @Test
  public void mapIfFullyMatchedOnMatched() {
    Optional<Integer> result = Strings.match("\\d+").on("123").mapIfFullyMatched(m -> 1);

    Assertions.assertThat(result).hasValue(1);
  }

  @Test
  public void mapIfFullyMatchedOnNonMatched() {
    Optional<Integer> result = Strings.match("a+").on("123").mapIfFullyMatched(m -> 1);

    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void groupsWithoutGroups() {
    Dictionary<String, Optional<String>> result = Strings.match("a+").on("123").groups();

    Assertions.assertThat(result.value()).isEmpty();
  }

  @Test
  public void groupsWithoutOneGroup() {
    Dictionary<String, Optional<String>> result = Strings.match("(?<t>.+)").on("123").groups();

    Assertions.assertThat(result.value())
        .containsOnly(Map.entry("t", Optional.of("123")));
  }

  @Test
  public void match() {
    Strings.match("(?<a>a+)(b+)(c+)?").on("aabbb").map(m -> {
      Assertions.assertThat(m.all()).isEqualTo("aabbb");
      Assertions.assertThat(m.mandatoryGroup(1)).isEqualTo("aa");
      Assertions.assertThat(m.mandatoryGroup(1, String::length)).isEqualTo(2);
      Assertions.assertThat(m.mandatoryGroup("a", String::length)).isEqualTo(2);
      Assertions.assertThat(m.mandatoryGroup("a")).isEqualTo("aa");

      Assertions.assertThat(m.mandatoryGroup(2)).isEqualTo("bbb");
      Assertions.assertThat(m.group(2, String::length)).hasValue(3);

      Assertions.assertThat(m.group(3)).isEmpty();
      return null;
    });
  }

}