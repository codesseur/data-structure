package com.codesseur.iterate;

import static java.util.function.Function.identity;

import com.codesseur.Person;
import com.codesseur.Persons;
import com.codesseur.Products;
import com.codesseur.iterate.container.Sequence;
import io.vavr.Tuple2;
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

    Stream<Integer> collect = persons.map(i -> -1, String::length, i -> -1, MappingPriority.FIRST);

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

    Assertions.assertThat((Stream<? extends String>) seq.remove(seq)).isEmpty();
  }

  @Test
  public void partialMinusOnSequence() {
    Persons seq1 = new Persons("v1", "v2", "v3");
    Persons seq2 = new Persons("v1", "v4");

    Assertions.assertThat((Stream<String>) seq1.remove(seq2)).containsOnly("v2", "v3");
  }

  @Test
  public void fullMinusOnBag() {
    Products bag = new Products("v1", "v2", "v3");

    Assertions.assertThat((Stream<? extends String>) bag.remove(bag)).isEmpty();
  }

  @Test
  public void partialMinusOnBag() {
    Products bag1 = new Products("v1", "v2", "v3");
    Products bag2 = new Products("v1", "v4");

    Assertions.assertThat((Stream<String>) bag1.remove(bag2)).containsOnly("v2", "v3");
  }

  @Test
  public void foldLeftWithSeed() {
    Persons persons = new Persons("v1", "v2", "v3");

    Assertions.assertThat(persons.foldLeft(() -> 0, (v, e) -> v + e.length())).isEqualTo(6);
  }

  @Test
  public void foldLeftWithSeedFunction() {
    Persons persons = new Persons("v1", "v2", "v3");

    Assertions.assertThat(persons.foldLeft(String::length, (v, e) -> v + e.length())).hasValue(6);
  }

  @Test
  public void foldLeft() {
    Persons persons = new Persons("v1", "v2", "v3");

    Assertions.assertThat(persons.foldLeft((v, v2) -> v + v2)).hasValue("v1v2v3");
  }

  @Test
  public void foldRightWithSeed() {
    Persons persons = new Persons("v1", "v2", "v3");

    Assertions.assertThat(persons.foldRight(() -> 0, (e, v) -> v + e.length())).isEqualTo(6);
  }

  @Test
  public void foldRightWithSeedFunction() {
    Persons persons = new Persons("v1", "v2", "v3");

    Assertions.assertThat(persons.foldRight(String::length, (e, v) -> v + e.length())).hasValue(6);
  }

  @Test
  public void foldRight() {
    Persons persons = new Persons("v1", "v2", "v3");

    Assertions.assertThat(persons.foldRight((v2, v) -> v + v2)).hasValue("v3v2v1");
  }

  @Test
  public void headTailLeadLastNotEmpty() {
    Persons persons = new Persons("v1", "v2", "v3");

    Assertions.assertThat(persons.head()).hasValue("v1");
    Assertions.assertThat((Stream<String>) persons.tail()).containsExactly("v2", "v3");
    Tuple2<Optional<String>, Streamed<String>> headAndTail = persons.headAndTail();
    Assertions.assertThat(headAndTail._1()).hasValue("v1");
    Assertions.assertThat((Stream<String>) headAndTail._2()).containsExactly("v2", "v3");

    Tuple2<Streamed<String>, Optional<String>> leadAndLAst = persons.leadAndLast();
    Assertions.assertThat(persons.last()).hasValue("v3");
    Assertions.assertThat((Stream<String>) persons.lead()).containsExactly("v1", "v2");
    Assertions.assertThat(leadAndLAst._2()).hasValue("v3");
    Assertions.assertThat((Stream<String>) leadAndLAst._1()).containsExactly("v1", "v2");
  }

  @Test
  public void headTailLeadLastEmpty() {
    Persons persons = new Persons();

    Assertions.assertThat(persons.head()).isEmpty();
    Assertions.assertThat((Stream<String>) persons.tail()).isEmpty();
    Tuple2<Optional<String>, Streamed<String>> headAndTail = persons.headAndTail();
    Assertions.assertThat(headAndTail._1()).isEmpty();
    Assertions.assertThat((Stream<String>) headAndTail._2()).isEmpty();

    Tuple2<Streamed<String>, Optional<String>> leadAndLAst = persons.leadAndLast();
    Assertions.assertThat(persons.last()).isEmpty();
    Assertions.assertThat((Stream<String>) persons.lead()).isEmpty();
    Assertions.assertThat(leadAndLAst._2()).isEmpty();
    Assertions.assertThat((Stream<String>) leadAndLAst._1()).isEmpty();
  }

  @Test
  public void headTailLeadLastWithOneElement() {
    Persons persons = new Persons("v1");

    Assertions.assertThat(persons.head()).hasValue("v1");
    Assertions.assertThat((Stream<String>) persons.tail()).isEmpty();
    Tuple2<Optional<String>, Streamed<String>> headAndTail = persons.headAndTail();
    Assertions.assertThat(headAndTail._1()).hasValue("v1");
    Assertions.assertThat((Stream<String>) headAndTail._2()).isEmpty();

    Tuple2<Streamed<String>, Optional<String>> leadAndLAst = persons.leadAndLast();
    Assertions.assertThat(persons.last()).hasValue("v1");
    Assertions.assertThat((Iterable<String>) persons.lead()).isEmpty();
    Assertions.assertThat(leadAndLAst._2()).hasValue("v1");
    Assertions.assertThat((Stream<String>) leadAndLAst._1()).isEmpty();
  }

  @Test
  public void splitWithIgnoreMode() {
    Persons persons = new Persons("v1", " ", "v2");

    List<List<String>> split = persons.split(v -> v.equals(" "), SplitMode.IGNORE).map(Streamed::toList).toList();

    Assertions.assertThat(split).containsExactly(List.of("v1"), List.of("v2"));
  }

  @Test
  public void splitWithLeftMode() {
    Persons persons = new Persons("v1", " ", "v2");

    List<List<String>> split = persons.split(v -> v.equals(" "), SplitMode.LEFT).map(Streamed::toList).toList();

    Assertions.assertThat(split).containsExactly(List.of("v1", " "), List.of("v2"));
  }

  @Test
  public void splitWithRightMode() {
    Persons persons = new Persons("v1", " ", "v2");

    List<List<String>> split = persons.split(v -> v.equals(" "), SplitMode.RIGHT).map(Streamed::toList).toList();

    Assertions.assertThat(split).containsExactly(List.of("v1"), List.of(" ", "v2"));
  }

  @Test
  public void splitBySize2() {
    Persons persons = new Persons("v1", " ", "v2");

    List<List<String>> split = persons.split(2).map(Streamed::toList).toList();

    Assertions.assertThat(split).containsExactly(List.of("v1", " "), List.of("v2"));
  }

  @Test
  public void splitBySize3() {
    Persons persons = new Persons("v1", " ", "v2");

    List<List<String>> split = persons.split(3).map(Streamed::toList).toList();

    Assertions.assertThat(split).containsExactly(List.of("v1", " ", "v2"));
  }

  @Test
  public void findLeftClosestExactMatch() {
    Sequence<Person> persons = Sequence.of(new Person("p1"), new Person("p2"), new Person("p3"));

    Optional<Person> split = persons.findLeftClosest(p -> p.name().compareTo("p2"));

    Assertions.assertThat(split).hasValue(new Person("p2"));
  }

  @Test
  public void findRightClosestExactMatch() {
    Sequence<Person> persons = Sequence.of(new Person("p1"), new Person("p2"), new Person("p3"));

    Optional<Person> split = persons.findRightClosest(p -> p.name().compareTo("p2"));

    Assertions.assertThat(split).hasValue(new Person("p2"));
  }

  @Test
  public void findLeftClosest() {
    Sequence<Person> persons = Sequence.of(new Person("p1"), new Person("p3"), new Person("p5"));

    Optional<Person> split = persons.findLeftClosest(p -> p.name().compareTo("p2"));

    Assertions.assertThat(split).hasValue(new Person("p1"));
  }

  @Test
  public void findRightClosest() {
    Sequence<Person> persons = Sequence.of(new Person("p1"), new Person("p3"), new Person("p5"));

    Optional<Person> split = persons.findRightClosest(p -> p.name().compareTo("p2"));

    Assertions.assertThat(split).hasValue(new Person("p3"));
  }

  @Test
  public void findRightClosestWithDuplicate() {
    Sequence<Person> persons = Sequence.of(new Person("p1"), new Person("p3"), new Person("p3"), new Person("p5"));

    Optional<Person> split = persons.findRightClosest(p -> p.name().compareTo("p2"));

    Assertions.assertThat(split).hasValue(new Person("p3"));
  }

  @Test
  public void findRightClosestAfterLast() {
    Sequence<Person> persons = Sequence.of(new Person("p1"), new Person("p3"), new Person("p5"));

    Optional<Person> split = persons.findRightClosest(p -> p.name().compareTo("p6"));

    Assertions.assertThat(split).isEmpty();
  }

  @Test
  public void findLeftClosestBeforeFirst() {
    Sequence<Person> persons = Sequence.of(new Person("p1"), new Person("p3"), new Person("p5"));

    Optional<Person> split = persons.findLeftClosest(p -> p.name().compareTo("p0"));

    Assertions.assertThat(split).isEmpty();
  }
}