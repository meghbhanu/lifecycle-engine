package io.github.meghbhanu.lifecycle.engine;

import io.github.meghbhanu.lifecycle.domain.Money;

import java.time.LocalDate;
import java.util.Objects;

public record CouponPaid(String productId, LocalDate date, Money amount )
            implements LifecycleEvent {
    public CouponPaid {
        Objects.requireNonNull(productId, "productId");
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(amount, "amount");

        if (productId.isBlank()) {
            throw new IllegalArgumentException("productId cannot be blank");
        }
        if (amount.amount().signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive, found %s"
                    .formatted(amount.amount()));
        }
    }
}
