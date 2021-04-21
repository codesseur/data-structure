package com.codesseur.mixin.iterate;

import java.util.function.BiPredicate;

public enum ZipMode implements BiPredicate<Boolean, Boolean> {
  UNION {
    @Override
    public boolean test(Boolean v1, Boolean v2) {
      return v1 || v2;
    }
  }, INTERSECT {
    @Override
    public boolean test(Boolean v1, Boolean v2) {
      return v1 && v2;
    }
  };

}
