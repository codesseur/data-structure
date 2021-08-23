package com.codesseur.iterate;

import com.codesseur.iterate.container.Dictionary;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class DictionaryTest {

  private Dictionary<String, String> emptyDictionary = Collections::emptyMap;
  private Dictionary<String, String> dictionary = () -> {
    Map<String, String> map = new HashMap<>();
    map.put("key", "value");
    return map;
  };

  @Test
  public void empty() {
    Assertions.assertThat(Dictionary.empty().size()).isEqualTo(0);
  }

  @Test
  public void size() {
    Assertions.assertThat(dictionary.size()).isEqualTo(1);
  }

  @Test
  public void hasKey() {
    Assertions.assertThat(dictionary.hasKey("key")).isTrue();
  }

  @Test
  public void keys() {
    Assertions.assertThat(dictionary.keys().value()).containsOnly("key");
  }

  @Test
  public void mapValue() {
    Assertions.assertThat(dictionary.mapValue(v -> v + v).value()).containsEntry("key", "valuevalue");
  }

  @Test
  public void mapKey() {
    Assertions.assertThat(dictionary.mapKey(k -> k + k).value()).containsEntry("keykey", "value");
  }

  @Test
  public void mapBi() {
    Assertions.assertThat((Iterable<String>) dictionary.map((k, v) -> k + k)).containsOnly("keykey");
  }

  @Test
  public void mapPartial() {
    Assertions.assertThat((Iterable<Object>) dictionary.mapPartial((k, v) -> Optional.empty())).isEmpty();
  }

  @Test
  public void mapNullValues() {
    Dictionary<String, String> dictionary = () -> {
      Map<String, String> map = new HashMap<>();
      map.put("key", null);
      return map;
    };

    Assertions.assertThat(dictionary.map(k -> k + k, v -> v).value()).containsEntry("keykey", null);
  }

  @Test
  public void filterKey() {
    Dictionary<String, String> dictionary = () -> {
      Map<String, String> map = new HashMap<>();
      map.put("key1", "value1");
      map.put("key2", "value2");
      return map;
    };

    Assertions.assertThat(dictionary.filterKey("key1"::equals).value()).containsOnlyKeys("key1");
  }

  @Test
  public void filterValue() {
    Dictionary<String, String> dictionary = () -> {
      Map<String, String> map = new HashMap<>();
      map.put("key1", "value1");
      map.put("key2", "value2");
      return map;
    };

    Assertions.assertThat(dictionary.filterValue("value2"::equals).value()).containsOnlyKeys("key2");
  }

  @Test
  public void mergeSelf() {
    Assertions.assertThat(dictionary.merge(dictionary).value()).containsEntry("key", "value");
  }

  @Test
  public void mergeEmpty() {
    Assertions.assertThat(dictionary.merge(emptyDictionary).value()).containsEntry("key", "value");
  }

  @Test
  public void isEmptyWithEmptyContainer() {
    Assertions.assertThat(emptyDictionary.isEmpty()).isTrue();
  }

  @Test
  public void isEmptyWithNonEmptyContainer() {
    Assertions.assertThat(dictionary.isEmpty()).isFalse();
  }

  @Test
  public void isNotEmptyWithEmptyContainer() {
    Assertions.assertThat(emptyDictionary.isNotEmpty()).isFalse();
  }

  @Test
  public void isNotEmptyWithNonEmptyContainer() {
    Assertions.assertThat(dictionary.isNotEmpty()).isTrue();
  }

}