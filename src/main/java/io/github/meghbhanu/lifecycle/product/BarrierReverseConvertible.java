package io.github.meghbhanu.lifecycle.product;

import io.github.meghbhanu.lifecycle.domain.CommonTerms;
import io.github.meghbhanu.lifecycle.domain.Percentage;

import java.math.BigDecimal;
import java.util.Objects;

public record BarrierReverseConvertible(CommonTerms terms, Percentage capitalBarrier) implements Product {
    public BarrierReverseConvertible {
        Objects.requireNonNull(terms, "terms");
        Objects.requireNonNull(capitalBarrier, "capitalBarrier");
        if (capitalBarrier.fraction().compareTo(BigDecimal.ONE) >= 0) {
            throw new IllegalArgumentException(
                    "capital barrier must be below 100%%, found %s".formatted(
                            capitalBarrier.toString()
                    )
            );
        }
    }
}
