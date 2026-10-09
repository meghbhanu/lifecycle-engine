package io.github.meghbhanu.lifecycle.payoff;

import io.github.meghbhanu.lifecycle.domain.Money;

import java.util.Objects;

public record Missed(Money coupon) implements ObservationOutcome {
    public Missed {
        Objects.requireNonNull(coupon, "coupon");
        if (coupon.amount().signum() <= 0) {
            throw new IllegalArgumentException("coupon must be > 0");
        }
    }
}
