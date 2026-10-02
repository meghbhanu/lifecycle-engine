package io.github.meghbhanu.lifecycle.domain;

import java.time.LocalDate;
import java.util.Objects;

public record ProductTerms(String productId, Underlying underlying,
                           Money notional, Money initialPrice, Percentage couponRate,
                           Percentage couponBarrier, Percentage autocallTrigger,
                           Percentage capitalBarrier, LocalDate issueDate,
                           LocalDate maturityDate, ObservationFrequency frequency) {

    public ProductTerms {
        Objects.requireNonNull(productId, "productId");
        Objects.requireNonNull(underlying, "underlying");
        Objects.requireNonNull(notional, "notional");
        Objects.requireNonNull(initialPrice, "initialPrice");
        Objects.requireNonNull(couponRate, "couponRate");
        Objects.requireNonNull(couponBarrier, "couponBarrier");
        Objects.requireNonNull(autocallTrigger, "autocallTrigger");
        Objects.requireNonNull(capitalBarrier, "capitalBarrier");
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
        if (capitalBarrier.fraction().compareTo(couponBarrier.fraction()) > 0) {
            throw new IllegalArgumentException(
                    "capital barrier %s must be at or below coupon barrier %s"
                            .formatted(capitalBarrier, couponBarrier));
        }
        if (couponBarrier.fraction().compareTo(autocallTrigger.fraction()) > 0) {
            throw new IllegalArgumentException(
                    "coupon barrier %s must be at or below autocallTrigger %s"
                            .formatted(couponBarrier, autocallTrigger));
        }
    }
}
