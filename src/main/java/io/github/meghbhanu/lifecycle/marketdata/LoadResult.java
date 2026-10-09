package io.github.meghbhanu.lifecycle.marketdata;

import java.util.List;
import java.util.Objects;

public record LoadResult<T>(T value, List<LoadError> errors) {
    public LoadResult {
        Objects.requireNonNull(value, "value");
        errors = List.copyOf(errors);       // the defensive copy, as always
    }

    public boolean hasErrors() { return !errors.isEmpty(); }
}
