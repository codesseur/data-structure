package com.codesseur.string;

import com.codesseur.functions.Functions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

public interface Join {

  static <T> Collector<String, ?, T> join(
      BinaryOperator<String> foreach,
      Function<Optional<String>, T> then) {
    return Collector.of(
        ArrayList::new,
        List::add,
        Functions.<List<String>, List<String>>func(List::addAll)::apply,
        strings -> then.apply(strings.stream().reduce(foreach)));
  }

  static Joiner join() {
    return new Joiner(false);
  }

  static Joiner joinIgnoringPrefixAndSuffixIfEmpty() {
    return new Joiner(true);
  }

  class Joiner {

    private final boolean ignorePrefixAndSuffixIfEmpty;

    public Joiner(boolean ignorePrefixAndSuffixIfEmpty) {
      this.ignorePrefixAndSuffixIfEmpty = ignorePrefixAndSuffixIfEmpty;
    }

    public Collector<String, ?, String> byLinePrefixed(String prefix) {
      return byLine(prefix, "");
    }

    public Collector<String, ?, String> byLineSuffixed(String suffix) {
      return byLine("", suffix);
    }

    public Collector<String, ?, String> byLine() {
      return byLine("", "");
    }

    public Collector<String, ?, String> byLine(String prefix, String suffix) {
      return byLines(1, prefix, suffix);
    }

    public Collector<String, ?, String> byLines(int lines) {
      return byLines(lines, "", "");
    }

    public Collector<String, ?, String> byLines(int lines, String prefix, String suffix) {
      String delimiter = Strings.repeat("\n", lines);
      return delimited(delimiter, prefix, suffix);
    }

    public Collector<String, ?, String> delimitedPrefixed(String delimiter, String prefix) {
      return delimited(delimiter, prefix, "");
    }

    public Collector<String, ?, String> delimitedSuffixed(String delimiter, String suffix) {
      return delimited(delimiter, "", suffix);
    }

    public Collector<String, ?, String> delimited(String delimiter) {
      return delimited(delimiter, "", "");
    }

    public Collector<String, ?, String> delimited(String delimiter, String prefix, String suffix) {
      return join((s1, s2) -> s1 + delimiter + s2,
          o -> o.or(() -> Optional.of("").filter(i -> !ignorePrefixAndSuffixIfEmpty)).map(s -> prefix + s + suffix).orElse(""));
    }

  }

}
