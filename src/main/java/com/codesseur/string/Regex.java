package com.codesseur.string;

import java.util.regex.Pattern;

public class Regex {

  private final Pattern pattern;

  public Regex(Pattern pattern) {
    this.pattern = pattern;
  }

  public MatchExtractor on(String text) {
    return new MatchExtractor(pattern, text);
  }
}
