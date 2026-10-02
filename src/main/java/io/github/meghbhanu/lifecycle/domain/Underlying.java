package io.github.meghbhanu.lifecycle.domain;

import java.util.Locale;
import java.util.Objects;

public record Underlying(String ticker, Currency currency) {
    public Underlying {
        Objects.requireNonNull(ticker, "ticker");
        Objects.requireNonNull(currency, "currency");

        if (ticker.isBlank()) {
            throw new IllegalArgumentException(
                    "ticker cannot be an empty string"
            );
        }

        ticker = ticker.strip().toUpperCase(Locale.ROOT);
    }
}
