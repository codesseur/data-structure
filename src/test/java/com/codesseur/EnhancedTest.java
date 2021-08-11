package com.codesseur;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnhancedTest {

  private Person person = new Person();

  @Test
  public void thenMap() {
    String name = person.then().apply(Person::name);

    Assertions.assertThat(name).isEqualTo(person.name());
  }

  @Test
  public void thenMatchFalse() {
    boolean result = person.then().matches(p -> p.name().equals("blabla"));

    Assertions.assertThat(result).isFalse();
  }

  @Test
  public void thenMatchTrue() {
    boolean result = person.then().matches(p -> p.name().equals(p.name()));

    Assertions.assertThat(result).isTrue();
  }

  @Test
  public void thenFilterEmpty() {
    Optional<Person> result = this.person.then().toOptional(p -> p.name().equals("blabla"));

    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void thenFilterNotEmpty() {
    Optional<Person> result = this.person.then().toOptional(p -> p.name().equals(p.name()));

    Assertions.assertThat(result).hasValue(person);
  }

  @Test
  public void thenStream() {
    Stream<Person> result = this.person.then().toStream();

    Assertions.assertThat(result).contains(person);
  }

  @Test
  public void thenPeek() {
    List<Person> persons = new ArrayList<>();
    Person result = this.person.then().accept(persons::add);

    Assertions.assertThat(persons).contains(person);
    Assertions.assertThat(result).isEqualTo(person);
  }
}