package io.github.meghbhanu.lifecycle.engine;

import java.time.LocalDate;
import java.util.Objects;

public record PriceMissing(String productId, LocalDate date)
        implements LifecycleEvent {
    public PriceMissing {
        Objects.requireNonNull(productId, "productId");
        Objects.requireNonNull(date, "date");

        if (productId.isBlank()) {
            throw new IllegalArgumentException("productId cannot be blank");
        }
    }
}