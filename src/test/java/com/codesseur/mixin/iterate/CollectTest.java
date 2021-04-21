package com.codesseur.mixin.iterate;

import com.codesseur.mixin.iterate.container.Bag;
import com.codesseur.mixin.iterate.container.Sequence;
import com.codesseur.mixin.Persons;
import com.codesseur.mixin.Products;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CollectTest {

  @Test
  public void toSimpleSequence() {
    Persons persons = new Persons("maria", "bob", "john");

    Sequence<String> collect = persons.stream().collect(Collect.toSequence());

    Assertions.assertThat(collect.value()).isEqualTo(persons.value());
  }

  @Test
  public void toSequenceWithFactory() {
    Persons persons = new Persons("maria", "bob", "john");

    Persons collect = persons.stream().collect(Collect.toSequence(Persons::new));

    Assertions.assertThat((Object) collect).isEqualTo(persons);
  }

  @Test
  public void toSimpleBag() {
    Products products = new Products("pen", "book");

    Bag<String> collect = products.stream().collect(Collect.toBag());

    Assertions.assertThat(collect.value()).isEqualTo(products.value());
  }

  @Test
  public void toBagWithFactory() {
    Products products = new Products("pen", "book");

    Products collect = products.stream().collect(Collect.toBag(Products::new));

    Assertions.assertThat((Object) collect).isEqualTo(products);
  }
}