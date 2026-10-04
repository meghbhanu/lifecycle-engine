package io.github.meghbhanu.lifecycle.domain;

import java.time.LocalDate;

public final class TestTerms {
    private TestTerms() {}   // utility class: no instances

    public static CommonTerms common(String productId) {
        return new CommonTerms(productId,
                new Underlying("AAPL", Currency.USD),
                Money.of("1000000", Currency.USD),
                Money.of("200", Currency.USD),
                Percentage.ofPercent("8"),
                LocalDate.of(2026, 10, 2),
                LocalDate.of(2029, 10, 2),
                ObservationFrequency.QUARTERLY);
    }
}