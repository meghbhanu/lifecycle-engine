package io.github.meghbhanu.lifecycle.product;

import io.github.meghbhanu.lifecycle.domain.CommonTerms;

import java.util.Objects;

public record FixedCouponNote(CommonTerms terms) implements Product {
    public FixedCouponNote {
        Objects.requireNonNull(terms, "terms");
    }
}
