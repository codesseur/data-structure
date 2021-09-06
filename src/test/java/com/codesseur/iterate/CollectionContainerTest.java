package com.codesseur.iterate;

import static java.util.function.Function.identity;

import com.codesseur.Persons;
import com.codesseur.Products;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CollectionContainerTest {

  @Test
  public void map() {
    Persons persons = new Persons("maria", "bob");

    Stream<Integer> collect = persons.map(String::length);

    Assertions.assertThat(collect).containsOnly(5, 3);
  }

  @Test
  public void mapLast() {
    Persons persons = new Persons("maria", "bob");

    Stream<Integer> collect = persons.mapLastOtherwise(i -> -1, String::length);

    Assertions.assertThat(collect).containsOnly(5, -1);
  }

  @Test
  public void mapFirst() {
    Persons persons = new Persons("maria", "bob");

    Stream<Integer> collect = persons.mapFirstOtherwise(i -> -1, String::length);

    Assertions.assertThat(collect).containsOnly(-1, 3);
  }

  @Test
  public void mapFirstMiddleLast() {
    Persons persons = new Persons("maria", "bob", "dima");

    Stream<Integer> collect = persons.map(i -> -1, String::length, i -> -1);

    Assertions.assertThat(collect).containsOnly(-1, 3, -1);
  }

  @Test
  public void flatMap() {
    Persons persons = new Persons("maria", "bob");

    Stream<Integer> collect = persons.flatMap(s -> Stream.of(s.length()));

    Assertions.assertThat(collect).containsOnly(5, 3);
  }

  @Test
  public void flatMapIterable() {
    Persons persons = new Persons("maria", "bob");

    Stream<Integer> collect = persons.flatMapIterable(s -> Collections.singleton(s.length()));

    Assertions.assertThat(collect).containsOnly(5, 3);
  }

  @Test
  public void mapPartial() {
    Persons persons = new Persons("maria", "bob");

    Stream<Integer> collect = persons.mapPartial(s -> Optional.of(s.length()));

    Assertions.assertThat(collect).containsOnly(5, 3);
  }

  @Test
  public void filter() {
    Persons persons = new Persons("maria", "bob", "");

    Stream<String> collect = persons.filter(String::isEmpty);

    Assertions.assertThat(collect).containsOnly("");
  }

  @Test
  public void join() {
    Persons persons1 = new Persons("maria", "bob");
    Persons persons2 = new Persons("maria", "bob");

    Stream<String> stream = persons1.join(persons2).by(identity(), identity())
        .combineAll((s1, s2) -> s1 + s2);

    Assertions.assertThat(stream).containsOnly("mariamaria", "bobbob");
  }

  @Test
  public void join2() {
    Persons persons1 = new Persons("maria", "bob", "john");
    Persons persons2 = new Persons("maria", "bob", "alice");

    Stream<String> stream = persons1.join(persons2).by(identity(), identity())
        .combine(s -> s, (s1, s2) -> s1 + s2, s -> s);

    Assertions.assertThat(stream).containsOnly("mariamaria", "bobbob", "john", "alice");
  }

  @Test
  public void join3() {
    Persons persons1 = new Persons("maria", "bob", "john");
    Persons persons2 = new Persons("maria", "bob");

    Stream<String> stream = persons1.join(persons2).by(identity(), identity())
        .combine(s -> s, (s1, s2) -> s1 + s2, s -> s);

    Assertions.assertThat(stream).containsOnly("mariamaria", "bobbob", "john");
  }

  @Test
  public void join4() {
    Persons persons1 = new Persons("maria", "bob", "john");
    Persons persons2 = new Persons("maria", "bob", "alice");

    Stream<String> stream = persons1.join(persons2).by(identity(), identity())
        .combine(String::toUpperCase, (s1, s2) -> s1 + s2,
            s -> s.concat(" au pays des merveilles"));

    Assertions.assertThat(stream)
        .containsOnly("mariamaria", "bobbob", "JOHN", "alice au pays des merveilles");
  }

  @Test
  public void isEmptyWithEmptyContainer() {
    Persons persons = new Persons();

    Assertions.assertThat(persons.isEmpty()).isTrue();
  }

  @Test
  public void isEmptyWithNonEmptyContainer() {
    Persons persons = new Persons("");

    Assertions.assertThat(persons.isEmpty()).isFalse();
  }

  @Test
  public void isNotEmptyWithEmptyContainer() {
    Persons persons = new Persons();

    Assertions.assertThat(persons.isNotEmpty()).isFalse();
  }

  @Test
  public void isNotEmptyWithNonEmptyContainer() {
    Persons persons = new Persons("");

    Assertions.assertThat(persons.isNotEmpty()).isTrue();
  }

  @Test
  public void zipWithIndex() {
    Persons persons = new Persons("maria", "bob");

    List<Indexed<String>> personsOut = persons.zipWithIndex()
        .collect(Collectors.toList());

    Assertions.assertThat(personsOut).hasSize(2);
    Assertions.assertThat(personsOut.get(0).index()).isEqualTo(0);
    Assertions.assertThat(personsOut.get(0).value()).isEqualTo("maria");
    Assertions.assertThat(personsOut.get(1).index()).isEqualTo(1);
    Assertions.assertThat(personsOut.get(1).value()).isEqualTo("bob");
  }

  @Test
  public void count() {
    Persons persons = new Persons("v1");

    Assertions.assertThat(persons.count()).isEqualTo(1);
  }

  @Test
  public void containsValue() {
    Persons persons = new Persons("v1");

    Assertions.assertThat(persons.contains("v1")).isTrue();
  }

  @Test
  public void containsWithPredicate() {
    Persons persons = new Persons("v1");

    Assertions.assertThat(persons.contains("v1"::equals)).isTrue();
  }

  @Test
  public void notContains() {
    Persons persons = new Persons("v1");

    Assertions.assertThat(persons.contains("v2")).isFalse();
  }

  @Test
  public void notContainsWithPredicate() {
    Persons persons = new Persons("v1");

    Assertions.assertThat(persons.contains("v2"::equals)).isFalse();
  }

  @Test
  public void findOneValue() {
    Persons persons = new Persons("v1", "v2");

    Assertions.assertThat(persons.find("v1"::equals)).hasValue("v1");
  }

  @Test
  public void findEmpty() {
    Persons persons = new Persons("v1");

    Assertions.assertThat(persons.find("v2"::equals)).isEmpty();
  }

  @Test
  public void distinct() {
    Persons persons = new Persons("v1", "v2", "v3");

    Assertions.assertThat((Iterable<String>) persons.distinctBy(String::length)).containsOnly("v1");
  }

  @Test
  public void fullMinusOnSequence() {
    Persons seq = new Persons("v1", "v2", "v3");

    Assertions.assertThat((Stream<? extends String>) seq.minus(seq)).isEmpty();
  }

  @Test
  public void partialMinusOnSequence() {
    Persons seq1 = new Persons("v1", "v2", "v3");
    Persons seq2 = new Persons("v1", "v4");

    Assertions.assertThat((Stream<String>) seq1.minus(seq2)).containsOnly("v2", "v3");
  }

  @Test
  public void fullMinusOnBag() {
    Products bag = new Products("v1", "v2", "v3");

    Assertions.assertThat((Stream<? extends String>) bag.minus(bag)).isEmpty();
  }

  @Test
  public void partialMinusOnBag() {
    Products bag1 = new Products("v1", "v2", "v3");
    Products bag2 = new Products("v1", "v4");

    Assertions.assertThat((Stream<String>) bag1.minus(bag2)).containsOnly("v2", "v3");
  }

}