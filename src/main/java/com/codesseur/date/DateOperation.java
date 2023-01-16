package com.codesseur.date;

import java.time.LocalDateTime;
import java.util.Optional;

@FunctionalInterface
public interface DateOperation {

  Optional<LocalDateTime> apply(LocalDateTime dateTime);
}
