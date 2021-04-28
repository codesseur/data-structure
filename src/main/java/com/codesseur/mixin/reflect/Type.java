package com.codesseur.mixin.reflect;

import static com.codesseur.mixin.iterate.Collect.toSequence;

import com.codesseur.mixin.Optionals;
import com.codesseur.mixin.SafeCaster;
import com.codesseur.mixin.iterate.Collect;
import com.codesseur.mixin.iterate.container.Sequence;
import io.vavr.control.Try;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class Type<T> implements SafeCaster {

  private final java.lang.reflect.Type value;
  private final Sequence<Type<?>> typeVariables;
  private final Class<T> raw;

  private Type(java.lang.reflect.Type value) {
    this.value = value;
    this.raw = getRaw();
    this.typeVariables = getTypeVariables();
  }

  public Type() {
    java.lang.reflect.Type superClass = this.getClass().getGenericSuperclass();
    if (superClass instanceof Class) {
      throw new IllegalArgumentException("Type constructed without actual type information");
    }
    value = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    this.raw = getRaw();
    this.typeVariables = getTypeVariables();
  }

  public static <T> Type<T> of(Class<T> type) {
    return of((java.lang.reflect.Type) type);
  }

  public static <T> Type<T> of(java.lang.reflect.Type type) {
    return new Type<>(type) {
    };
  }

  private Class<T> getRaw() {
    return raw(this.value)
        .orElseThrow(() -> new IllegalStateException("no class found for" + value));
  }

  private Sequence<Type<?>> getTypeVariables() {
    java.lang.reflect.Type[] types = safeCast(value, ParameterizedType.class)
        .map(ParameterizedType::getActualTypeArguments)
        .orElseGet(() -> new java.lang.reflect.Type[0]);
    return Stream.of(types).map(Type::of).collect(Collect.toSequence());
  }

  public Class<T> raw() {
    return raw;
  }

  public Sequence<Type<?>> typeVariables() {
    return typeVariables;
  }

  public boolean isInstance(Object object) {
    return raw(value).map(type -> type.isInstance(object)).orElse(false);
  }

  public boolean is(java.lang.reflect.Type type) {
    return equals(Type.of(type));
  }

  public boolean isSuperTypeOf(java.lang.reflect.Type type) {
    return raw.isAssignableFrom(Type.of(type).raw);
  }

  private Optional<Class<T>> raw(java.lang.reflect.Type value) {
    return Optionals.or(
        safeCast(value, Class.class).map(c -> (Class<T>) c),
        safeCast(value, ParameterizedType.class).flatMap(t -> raw(t.getRawType())));
  }

  public <E> Optional<E> getIfEquals(Type<?> type, Function<Type<T>, E> mapper) {
    return equals(type) ? Optional.ofNullable(mapper.apply(this)) : Optional.empty();
  }

  public <E> Optional<E> getIfSameRaw(Type<?> type, Function<Type<T>, E> mapper) {
    return raw().equals(type.raw) ? Optional.ofNullable(mapper.apply(this)) : Optional.empty();
  }

  public T cast(Object object) {
    return raw().cast(object);
  }

  public Optional<T> safeCast(Object object) {
    return safeCast(object, raw());
  }

  public Sequence<Field> fields() {
    return getAllFieldsList().stream()
        .map(Field::new)
        .collect(Collect.toSequence());
  }

  private List<java.lang.reflect.Field> getAllFieldsList() {
    final List<java.lang.reflect.Field> allFields = new ArrayList<>();
    Class<?> currentClass = raw();
    while (currentClass != null) {
      final java.lang.reflect.Field[] declaredFields = currentClass.getDeclaredFields();
      Collections.addAll(allFields, declaredFields);
      currentClass = currentClass.getSuperclass();
    }
    return allFields;
  }

  public Try<T> newInstance() {
    return Try.of(() -> raw().newInstance());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Type)) {
      return false;
    }
    Type<?> type = (Type<?>) o;
    return raw().equals(type.raw()) && typeVariables().equals(type.typeVariables());
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  public abstract static class $<T> {

    @SafeVarargs
    public static <T> Type<T> $(@Deprecated T... unused) {
      if (unused == null || unused.length != 0) {
        throw new IllegalArgumentException("must be called with empty params");
      }
      return Type.of((Class<T>) unused.getClass().getComponentType());
    }

  }
}
