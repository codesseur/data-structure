package com.codesseur.string;

import static java.util.function.Function.identity;

import com.codesseur.iterate.Streamed;
import com.codesseur.iterate.container.Dictionary;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchExtractor {

  public static final String GROUP_PATTERN = "\\(\\?<(\\w+)>";

  private final Pattern pattern;
  private final String text;
  private final Matcher matcher;
  private boolean matched;

  public MatchExtractor(Pattern pattern, String text) {
    this.pattern = pattern;
    this.text = Optional.ofNullable(text).orElse("");
    matcher = pattern.matcher(this.text);
    matched = matcher.find();
  }

  public Dictionary<String, Optional<String>> groups() {
    return new MatchExtractor(Pattern.compile(GROUP_PATTERN), pattern.pattern())
        .map(m -> m.mandatoryGroup(1))
        .toDictionary2(group -> mapFirst(m -> m.group(group)).flatMap(identity()));
  }

  public <T> Optional<T> mapIfFullyMatched(Function<Match, T> mapper) {
    return isFullyMatched() ? mapFirst(mapper) : Optional.empty();
  }

  public <T> Optional<T> mapFirst(Function<Match, T> mapper) {
    return matched ? Optional.ofNullable(mapper.apply(new RegexMatch(matcher))) : Optional.empty();
  }

  public <T> Streamed<T> map(Function<Match, T> mapper) {
    RegexMatch match = new RegexMatch(matcher);
    List<T> results = new ArrayList<>();
    while (matched) {
      results.add(mapper.apply(match));
      matched = matcher.find();
    }
    return Streamed.of(results);
  }

  public String replaceRecursive(Function<Match, String> replacement) {
    final String result = replace(replacement);
    return result.equals(text) ? result : new MatchExtractor(pattern, result).replaceRecursive(replacement);
  }

  public String replace(Function<Match, String> replacement) {
    RegexMatch match = new RegexMatch(matcher);
    int diff = 0;
    String resolved = text;
    while (matched) {
      String result = replacement.apply(match);
      resolved = resolved.substring(0, matcher.start() + diff) + result + resolved.substring(matcher.end() + diff);
      diff += result.length() - (matcher.group().length());
      matched = matcher.find();
    }
    return resolved;
  }

  boolean isFullyMatched() {
    return matched && matcher.start() == 0 && matcher.end() == text.length();
  }

  public interface Match {

    String all();

    default String mandatoryGroup(String group) {
      return group(group).orElseThrow();
    }

    default <T> T mandatoryGroup(String group, Function<String, T> mapper) {
      return group(group, mapper).orElseThrow();
    }

    default <T> Optional<T> group(String group, Function<String, T> mapper) {
      return group(group).map(mapper);
    }

    Optional<String> group(String group);

    default String mandatoryGroup(int group) {
      return group(group).orElseThrow();
    }

    default <T> T mandatoryGroup(int group, Function<String, T> mapper) {
      return group(group, mapper).orElseThrow();
    }

    default <T> Optional<T> group(int group, Function<String, T> mapper) {
      return group(group).map(mapper);
    }

    Optional<String> group(int group);
  }

  static class RegexMatch implements Match {

    private final Matcher matcher;

    RegexMatch(Matcher matcher) {
      this.matcher = matcher;
    }

    @Override
    public String all() {
      return matcher.group();
    }

    @Override
    public Optional<String> group(String group) {
      return Optional.ofNullable(matcher.group(group));
    }

    @Override
    public Optional<String> group(int group) {
      return Optional.ofNullable(matcher.group(group));
    }
  }

}
