package io.github.meghbhanu.lifecycle.domain;

import java.time.LocalDate;
import java.util.Objects;

public record CommonTerms(String productId, Underlying underlying,
                          Money notional, Money initialPrice, Percentage couponRate,
                          LocalDate issueDate,
                          LocalDate maturityDate, ObservationFrequency frequency) {

    public CommonTerms {
        Objects.requireNonNull(productId, "productId");
        Objects.requireNonNull(underlying, "underlying");
        Objects.requireNonNull(notional, "notional");
        Objects.requireNonNull(initialPrice, "initialPrice");
        Objects.requireNonNull(couponRate, "couponRate");
        Objects.requireNonNull(issueDate, "issueDate");
        Objects.requireNonNull(maturityDate, "maturityDate");
        Objects.requireNonNull(frequency, "frequency");

        if (productId.isBlank()) {
            throw new IllegalArgumentException(
                    "productId cannot be an empty string"
            );
        }

        if (notional.amount().signum() <= 0) {
            throw new IllegalArgumentException(
                    "notional must be positive, got " + notional);
        }
        if (initialPrice.amount().signum() <= 0) {
            throw new IllegalArgumentException(
                    "initialPrice must be positive, got " + initialPrice);
        }
        if (!maturityDate.isAfter(issueDate)) {
            throw new IllegalArgumentException("maturity date must be after issue date");
        }
        if (initialPrice.currency() != underlying.currency()) {
            throw new IllegalArgumentException(
                    "initial price currency %s must match underlying currency %s"
                            .formatted(initialPrice.currency(), underlying.currency()));
        }
    }
}
