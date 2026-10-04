package io.github.meghbhanu.lifecycle.product;

import io.github.meghbhanu.lifecycle.domain.CommonTerms;
import io.github.meghbhanu.lifecycle.domain.Percentage;

import java.util.Objects;

public record Autocallable(CommonTerms terms, Percentage couponBarrier,
                           Percentage autocallTrigger, Percentage capitalBarrier) implements Product {
    public Autocallable {
        Objects.requireNonNull(terms, "terms");
        Objects.requireNonNull(couponBarrier, "couponBarrier");
        Objects.requireNonNull(autocallTrigger, "autocallTrigger");
        Objects.requireNonNull(capitalBarrier, "capitalBarrier");

        if (capitalBarrier.fraction().compareTo(couponBarrier.fraction()) > 0) {
            throw new IllegalArgumentException(
                    "capital barrier %s must be at or below coupon barrier %s"
                            .formatted(capitalBarrier, couponBarrier));
        }
        if (couponBarrier.fraction().compareTo(autocallTrigger.fraction()) > 0) {
            throw new IllegalArgumentException(
                    "coupon barrier %s must be at or below autocall trigger %s"
                            .formatted(couponBarrier, autocallTrigger));
        }
    }
}
