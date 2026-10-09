package io.github.meghbhanu.lifecycle.engine;

import io.github.meghbhanu.lifecycle.domain.Money;

import java.time.LocalDate;
import java.util.Objects;

public record Matured(String productId, LocalDate date, Money redemption )
        implements LifecycleEvent {
    public Matured {
        Objects.requireNonNull(productId, "productId");
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(redemption, "amount");

        if (productId.isBlank()) {
            throw new IllegalArgumentException("productId cannot be blank");
        }
        if (redemption.amount().signum() < 0) {
            throw new IllegalArgumentException("redemption must be positive, found %s"
                    .formatted(redemption.amount()));
        }
    }
}