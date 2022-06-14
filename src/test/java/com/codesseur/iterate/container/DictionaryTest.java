package com.codesseur.iterate.container;

import com.codesseur.iterate.Streamed;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
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
  public void putMaybeSimpleWithEmpty() {
    Dictionary<String, String> empty = Dictionary.empty();
    Dictionary<String, String> result = empty.putMaybe("k1", o -> {
      Assertions.assertThat(o).isEmpty();
      return o;
    });

    Assertions.assertThat(result.value()).isEmpty();
  }

  @Test
  public void putMaybeSimpleWithNewKey() {
    Dictionary<String, String> empty = Dictionary.empty();
    Dictionary<String, String> result = empty.putMaybe("k1", o -> {
      Assertions.assertThat(o).isEmpty();
      return Optional.of("v1");
    });

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "v1"));
  }

  @Test
  public void putMaybeSimpleWithOldAndNewKey() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.putMaybe("k1", o -> {
      Assertions.assertThat(o).hasValue("v1");
      return Optional.of("y1");
    });
    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "y1"));
  }

  @Test
  public void putMaybeWithEmpty() {
    Dictionary<String, String> empty = Dictionary.empty();
    Tuple2<Dictionary<String, String>, Optional<String>> result = empty.putMaybe("k1", o -> {
      Assertions.assertThat(o).isEmpty();
      return o;
    }, (o, v) -> {
      Assertions.fail("ifPut must not be called");
      return "";
    });

    Assertions.assertThat(result._1.value()).isEmpty();
    Assertions.assertThat(result._2).isEmpty();
  }

  @Test
  public void putMaybeWithNewKey() {
    Dictionary<String, String> empty = Dictionary.empty();
    Tuple2<Dictionary<String, String>, Optional<String>> result = empty.putMaybe("k1", o -> {
      Assertions.assertThat(o).isEmpty();
      return Optional.of("v1");
    }, (o, v) -> {
      Assertions.assertThat(o).isEmpty();
      Assertions.assertThat(v).isEqualTo("v1");
      return "w1";
    });

    Assertions.assertThat(result._1.value()).containsOnly(Map.entry("k1", "v1"));
    Assertions.assertThat(result._2).hasValue("w1");
  }

  @Test
  public void putMaybeWithOldAndNewKey() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Tuple2<Dictionary<String, String>, Optional<String>> result = dictionary.putMaybe("k1", o -> {
      Assertions.assertThat(o).hasValue("v1");
      return Optional.of("y1");
    }, (o, v) -> {
      Assertions.assertThat(o).hasValue("v1");
      Assertions.assertThat(v).isEqualTo("y1");
      return "w1";
    });
    Assertions.assertThat(result._1.value()).containsOnly(Map.entry("k1", "y1"));
    Assertions.assertThat(result._2).hasValue("w1");
  }

  @Test
  public void putNewKey() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.put("k2", "y1");

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "v1"), Map.entry("k2", "y1"));
  }

  @Test
  public void putOldKey() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.put("k1", "y1");

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "y1"));
  }

  @Test
  public void putNewKeyWithFunction() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.put("k2", o -> {
      Assertions.assertThat(o).isEmpty();
      return "y1";
    });

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "v1"), Map.entry("k2", "y1"));
  }

  @Test
  public void putOldKeyWithFunction() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.put("k1", o -> {
      Assertions.assertThat(o).hasValue("v1");
      return "y1";
    });

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "y1"));
  }

  @Test
  public void putNewKeyWithSupplier() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.put("k2", v -> {
      Assertions.fail("canot be here");
      return "y1";
    }, () -> "w1");

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "v1"), Map.entry("k2", "w1"));
  }

  @Test
  public void putOldKeyWithSupplier() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.put("k1", v -> {
      Assertions.assertThat(v).isEqualTo("v1");
      return "y1";
    }, () -> {
      Assertions.fail("canot be here");
      return "w1";
    });

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "y1"));
  }

  @Test
  public void replaceOldKey() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.replace("k1", "y1");

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "y1"));
  }

  @Test
  public void replaceNewKey() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.replace("k2", "y1");

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "v1"));
  }

  @Test
  public void replaceOldKeyWithFunction() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.replace("k1", v -> {
      Assertions.assertThat(v).isEqualTo("v1");
      return "y1";
    });

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "y1"));
  }

  @Test
  public void replaceNewKeyWithFunction() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.replace("k2", v -> {
      Assertions.fail("cannot be here");
      return "y1";
    });

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "v1"));
  }

  @Test
  public void replaceOldKeyWithFunctionAndIfReplaced() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Tuple2<Dictionary<String, String>, Optional<String>> result = dictionary.replace("k1", v -> {
      Assertions.assertThat(v).isEqualTo("v1");
      return "y1";
    }, (o, n) -> o + n);

    Assertions.assertThat(result._1().value()).containsOnly(Map.entry("k1", "y1"));
    Assertions.assertThat(result._2()).hasValue("v1y1");
  }

  @Test
  public void replaceNewKeyWithFunctionAndIfReplaced() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Tuple2<Dictionary<String, String>, Optional<String>> result = dictionary.replace("k2", v -> {
      Assertions.fail("cannot be here");
      return "y1";
    }, (o, n) -> {
      Assertions.fail("cannot be here");
      return o + n;
    });

    Assertions.assertThat(result._1().value()).containsOnly(Map.entry("k1", "v1"));
    Assertions.assertThat(result._2()).isEmpty();

  }

  @Test
  public void replaceIfPredicateMatched() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.replaceIf("v1"::equals, v -> {
      Assertions.assertThat(v).isEqualTo("v1");
      return "y1";
    });

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "y1"));
  }

  @Test
  public void replaceIfPredicateNotMatched() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.replaceIf("k2"::equals, v -> {
      Assertions.fail("cannot be here");
      return "y1";
    });

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "v1"));
  }

  @Test
  public void replaceIfBiPredicateMatched() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.replaceIf((k, v) -> "k1".equals(k), v -> {
      Assertions.assertThat(v).isEqualTo("v1");
      return "y1";
    });

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "y1"));
  }

  @Test
  public void replaceIfBiPredicateNotMatched() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.replaceIf((k, v) -> "k2".equals(k), v -> {
      Assertions.fail("cannot be here");
      return "y1";
    });

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "v1"));
  }

  @Test
  public void replaceIfBiPredicateMatchedWithBiFunction() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.replaceIf((k, v) -> "k1".equals(k), (k, v) -> {
      Assertions.assertThat(k).isEqualTo("k1");
      Assertions.assertThat(v).isEqualTo("v1");
      return "y1";
    });

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "y1"));
  }

  @Test
  public void replaceIfBiPredicateNotMatchedWithBiFunction() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));
    Dictionary<String, String> result = dictionary.replaceIf((k, v) -> "k2".equals(k), (k, v) -> {
      Assertions.fail("cannot be here");
      return "y1";
    });

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "v1"));
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

  @Test
  public void forEachKey() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));

    dictionary.forEachKey(k -> Assertions.assertThat(k).isIn("k1"));
  }

  @Test
  public void forEachValue() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));

    dictionary.forEachValue(v -> Assertions.assertThat(v).isIn("v1"));
  }

  @Test
  public void forEach() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));

    dictionary.forEach((k, v) -> {
      Assertions.assertThat(k).isIn("k1");
      Assertions.assertThat(v).isIn("v1");
    });
  }

  @Test
  public void values() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"));

    Assertions.assertThat(dictionary.values().value()).containsExactly("v1");
  }

  @Test
  public void removeExistingKey() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"), Map.entry("k2", "v2"));

    Dictionary<String, String> result = dictionary.remove("k2");

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "v1"));
  }

  @Test
  public void removeUnknownKey() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"), Map.entry("k2", "v2"));

    Dictionary<String, String> result = dictionary.remove("k3");

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "v1"), Map.entry("k2", "v2"));
  }

  @Test
  public void removeMultipleKeys() {
    Dictionary<String, String> dictionary = Dictionary.of(Map.entry("k1", "v1"), Map.entry("k2", "v2"));

    Dictionary<String, String> result = dictionary.remove(List.of("k1", "k2"));

    Assertions.assertThat(result.value()).isEmpty();
  }

  @Test
  public void joinByKey() {
    Dictionary<String, String> dictionary1 = Dictionary.of(Map.entry("k1", "v1"), Map.entry("k2", "v2"));
    Dictionary<String, String> dictionary2 = Dictionary.of(Map.entry("h1", "w1"), Map.entry("k2", "w2"));
    Function<Entry<String, String>, String> collapse = e -> e.getKey() + e.getValue();

    Sequence<String> result = dictionary1.joinByKey(dictionary2)
        .combine(collapse, (e1, e2) -> collapse.apply(e1) + collapse.apply(e2), collapse)
        .toSequence();

    Assertions.assertThat(result.value()).containsOnly("k1v1", "k2v2k2w2", "h1w1");
  }

  @Test
  public void ofTuples() {
    Dictionary<String, String> dictionary = Dictionary.of(Tuple.of("k1", "v1"));

    Assertions.assertThat(dictionary.value()).containsOnly(Map.entry("k1", "v1"));
  }

  @Test
  public void equals() {
    Dictionary<String, String> dictionary1 = Dictionary.of(Tuple.of("k1", "v1"));
    Dictionary<String, String> dictionary2 = Dictionary.of(Tuple.of("k1", "v1"));

    Assertions.assertThat(dictionary1).isEqualTo(dictionary2);
  }

  @Test
  public void notEquals() {
    Dictionary<String, String> dictionary1 = Dictionary.of(Tuple.of("k1", "v1"));
    Dictionary<String, String> dictionary2 = Dictionary.of(Tuple.of("k1", "v2"));

    Assertions.assertThat(dictionary1).isNotEqualTo(dictionary2);
  }

  @Test
  public void putMap() {
    Dictionary<String, String> empty = Dictionary.empty();
    Dictionary<String, String> result = empty.put(Map.of("k1", "v1"));

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "v1"));
  }

  @Test
  public void putDictionary() {
    Dictionary<String, String> empty = Dictionary.empty();
    Dictionary<String, String> result = empty.put(Dictionary.of(Tuple.of("k1", "v1")));

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "v1"));
  }

  @Test
  public void putIterable() {
    Dictionary<String, String> empty = Dictionary.empty();
    Dictionary<String, String> result = empty.put(s -> s, Streamed.of("k1", "k2"));

    Assertions.assertThat(result.value()).containsOnly(Map.entry("k1", "k1"), Map.entry("k2", "k2"));
  }
}