package io.github.meghbhanu.lifecycle.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");

        amount = amount.setScale(currency.decimalPlaces(), RoundingMode.HALF_EVEN);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(String amount, Currency currency) {
        return new Money(new BigDecimal(amount), currency);
    }

    public Money plus(Money other) {
        Objects.requireNonNull(other, "other");
        if (currency != other.currency) {
            throw new IllegalArgumentException(
                    "Cannot add %s to %s".formatted(other.currency, currency));
        }
        return new Money(amount.add(other.amount), currency);
    }

    public Money times(BigDecimal factor) {
        Objects.requireNonNull(factor, "factor");
        return new Money(amount.multiply(factor), currency);
    }
}
