package com.codesseur.reflect;

import com.codesseur.reflect.Type.$;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TypeTest {

  @Test
  void of() {
    Type<Cat> type = $.$();

    Assertions.assertThat((Stream<Field>) type.fields())
        .anyMatch(f -> f.name().equals("name"))
        .anyMatch(f -> f.name().equals("children"))
        .anyMatch(f -> f.name().equals("age"));
  }

  @Test
  void raw() {
    Type<Cat> type = $.$();

    Assertions.assertThat(type.raw()).isEqualTo(Cat.class);
  }

  @Test
  void typeVariables() {
    Type<Animal<Cat>> type = new Type<>() {
    };

    Assertions.assertThat((Stream<Type<?>>) type.typeVariables()).containsExactly($.<Cat>$());
  }

  @Test
  void isInstance() {
    Type<Cat> type = $.$();

    Cat cat = new Cat();

    Assertions.assertThat(type.isInstance(cat)).isTrue();
    Assertions.assertThat(type.isInstance("cat")).isFalse();
  }

  @Test
  void is() {
    Type<String> type = $.$();

    Assertions.assertThat(type.is(String.class)).isTrue();
  }

  @Test
  void isSuperTypeOf() {
    Type<Animal<Cat>> superType = $.$();

    Assertions.assertThat(superType.isSuperTypeOf(Cat.class)).isTrue();
  }

  @Test
  void getIfEquals() {
    Type<Animal<Cat>> type = new Type<>() {
    };
    Type<Animal<String>> type2 = new Type<>() {
    };

    Assertions.assertThat(type.getIfEquals(type, t -> true)).hasValue(true);
    Assertions.assertThat(type2.getIfEquals(type, t -> true)).isEmpty();
  }

  @Test
  void getIfSameRaw() {
    Type<Animal<Cat>> type = new Type<>() {
    };
    Type<Animal<String>> type2 = new Type<>() {
    };

    Assertions.assertThat(type.getIfSameRaw(type, t -> true)).hasValue(true);
    Assertions.assertThat(type2.getIfSameRaw(type, t -> true)).hasValue(true);
  }

  @Test
  void cast() {
  }
}