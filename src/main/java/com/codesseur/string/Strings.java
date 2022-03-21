package com.codesseur.string;

import static com.codesseur.string.Join.join;

import com.codesseur.iterate.Streamed;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Strings {

  static Regex match(String regex) {
    Objects.requireNonNull(regex, "regex cannot be null");
    return match(Pattern.compile(regex));
  }

  static Regex match(Pattern pattern) {
    Objects.requireNonNull(pattern, "pattern cannot be null");
    return new Regex(pattern);
  }

  static String rightPadMultiline(String text, int width) {
    return notEmpty(text) ? lines(text).map(l -> rightPad(l, width)).collect(join().byLine()) : text;
  }

  static String leftPadMultiline(String text, int width) {
    return notEmpty(text) ? lines(text).map(l -> leftPad(l, width)).collect(join().byLine()) : text;
  }

  static Streamed<String> lines(String text) {
    return Optional.ofNullable(text)
        .map(s -> {
          Streamed<String> splits = Streamed.of(s.split("\\n"));
          return s.endsWith("\n") ? splits.append("") : splits;
        })
        .orElseGet(Streamed::empty);
  }

  static String rightPad(String text, int width) {
    return String.format("%" + -width + "s", Optional.ofNullable(text).orElse(""));
  }

  static String leftPad(String text, int width) {
    return String.format("%" + width + "s", Optional.ofNullable(text).orElse(""));
  }

  static String repeat(String text, int times) {
    return IntStream.range(0, times).mapToObj(i -> text).collect(Collectors.joining());
  }

  static String betweenIfNotEmpty(String text, CharSequence prefix, CharSequence suffix) {
    return notEmpty(text) ? prefix + text + suffix : text;
  }

  static boolean notEmpty(String text) {
    return Optional.ofNullable(text).filter(s -> !s.isEmpty()).isPresent();
  }

  static boolean notBlank(String text) {
    return Optional.ofNullable(text).filter(s -> !s.trim().isEmpty()).isPresent();
  }

}
