package com.codesseur.mixin;

public class Person implements Enhanced<Person>, SafeCaster {

  public String name() {
    return "bob";
  }
}
