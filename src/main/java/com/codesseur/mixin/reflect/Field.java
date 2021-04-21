package com.codesseur.mixin.reflect;

import java.lang.annotation.Annotation;
import java.util.Optional;

public class Field {

  private final java.lang.reflect.Field field;

  public Field(java.lang.reflect.Field field) {
    this.field = field;
  }

  public java.lang.reflect.Field field() {
    return field;
  }

  public String name() {
    return field().getName();
  }

  public Type<?> type() {
    return Type.of(field().getGenericType());
  }

  public <T extends Annotation> Optional<T> annotation(Type<T> type) {
    return Optional.ofNullable(field().getAnnotation(type.raw()));
  }

}
