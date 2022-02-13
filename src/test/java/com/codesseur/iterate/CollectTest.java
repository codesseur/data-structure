package com.codesseur.iterate;

import static com.codesseur.iterate.Collect.toSequence;

import com.codesseur.Persons;
import com.codesseur.Products;
import com.codesseur.iterate.container.Bag;
import com.codesseur.iterate.container.Sequence;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CollectTest {

  @Test
  public void partitionOnlyLeft() {
    Streamed<Either<String, String>> values = Streamed.of(Either.left("v1"), Either.left("v2"));

    Tuple2<Sequence<String>, Sequence<String>> collected = values
        .collect(Collect.partition(toSequence(), toSequence()));

    Assertions.assertThat((Stream<String>) collected._1).containsExactly("v1", "v2");
    Assertions.assertThat((Stream<String>) collected._2).isEmpty();
  }

  @Test
  public void partitionOnlyRight() {
    Streamed<Either<String, String>> values = Streamed.of(Either.right("v1"), Either.right("v2"));

    Tuple2<Sequence<String>, Sequence<String>> collected = values
        .collect(Collect.partition(toSequence(), toSequence()));

    Assertions.assertThat((Stream<String>) collected._1).isEmpty();
    Assertions.assertThat((Stream<String>) collected._2).containsExactly("v1", "v2");
  }

  @Test
  public void partitionLeftRight() {
    Streamed<Either<String, String>> values = Streamed.of(Either.left("v1"), Either.right("v2"));

    Tuple2<Sequence<String>, Sequence<String>> collected = values
        .collect(Collect.partition(toSequence(), toSequence()));

    Assertions.assertThat((Stream<String>) collected._1).containsExactly("v1");
    Assertions.assertThat((Stream<String>) collected._2).containsExactly("v2");
  }

  @Test
  public void partitionLeftOnlyWithSplitter() {
    Streamed<String> values = Streamed.of("v1", "v2");

    Tuple2<Sequence<String>, Sequence<String>> collected = values
        .collect(Collect.partition(Either::left, toSequence(), toSequence()));

    Assertions.assertThat((Stream<String>) collected._1).containsExactly("v1", "v2");
    Assertions.assertThat((Stream<String>) collected._2).isEmpty();
  }

  @Test
  public void partitionRightOnlyWithSplitter() {
    Streamed<String> values = Streamed.of("v1", "v2");

    Tuple2<Sequence<String>, Sequence<String>> collected = values
        .collect(Collect.partition(Either::right, toSequence(), toSequence()));

    Assertions.assertThat((Stream<String>) collected._1).isEmpty();
    Assertions.assertThat((Stream<String>) collected._2).containsExactly("v1", "v2");
  }

  @Test
  public void partitionLeftRightWithSplitter() {
    Streamed<String> values = Streamed.of("v1", "v2");

    Tuple2<Sequence<String>, Sequence<String>> collected = values
        .collect(Collect.partition(v -> "v1".equals(v) ? Either.left(v) : Either.right(v), toSequence(), toSequence()));

    Assertions.assertThat((Stream<String>) collected._1).containsExactly("v1");
    Assertions.assertThat((Stream<String>) collected._2).containsExactly("v2");
  }

  @Test
  public void toSimpleSequence() {
    Persons persons = new Persons("maria", "bob", "john");

    Sequence<String> collect = persons.toSequence();

    Assertions.assertThat(collect.value()).isEqualTo(persons.value());
  }

  @Test
  public void toSequenceWithFactory() {
    Persons persons = new Persons("maria", "bob", "john");

    Persons collect = persons.toSequence(Persons::new);

    Assertions.assertThat((Object) collect).isEqualTo(persons);
  }

  @Test
  public void toSimpleBag() {
    Products products = new Products("pen", "book");

    Bag<String> collect = products.toBag();

    Assertions.assertThat(collect.value()).isEqualTo(products.value());
  }

  @Test
  public void toBagWithFactory() {
    Products products = new Products("pen", "book");

    Products collect = products.toBag(Products::new);

    Assertions.assertThat((Object) collect).isEqualTo(products);
  }
}