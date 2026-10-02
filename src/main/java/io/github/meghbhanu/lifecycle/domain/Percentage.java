package io.github.meghbhanu.lifecycle.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record Percentage(BigDecimal fraction) {
    public Percentage {
        Objects.requireNonNull(fraction, "fraction");
        if (fraction.signum() < 0 || fraction.compareTo(BigDecimal.TEN) > 0) {
            throw new IllegalArgumentException(
                    "Percentage must be between 0%% and 1000%%, got %s"
                            .formatted(fraction.movePointRight(2).toPlainString())
            );
        }

        fraction = fraction.stripTrailingZeros();
    }

    public static Percentage ofPercent(String percent) {
        return new Percentage(new BigDecimal(percent).movePointLeft(2));
    }

    public Money applyTo(Money money) {
        return money.times(fraction);
    }

    @Override
    public String toString() {
        return fraction.movePointRight(2).toPlainString() + "%";
    }
}
