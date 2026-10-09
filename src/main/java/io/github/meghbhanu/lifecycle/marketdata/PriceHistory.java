package io.github.meghbhanu.lifecycle.marketdata;

import io.github.meghbhanu.lifecycle.domain.Money;

import java.time.LocalDate;
import java.util.*;

public final class PriceHistory {
    private final NavigableMap<LocalDate, Money> prices;

    public PriceHistory(Map<LocalDate, Money> prices) {
        Objects.requireNonNull(prices, "prices");
        this.prices = Collections.unmodifiableNavigableMap(new TreeMap<>(prices));
    }

    public Optional<Money> priceOn(LocalDate date) {
        return Optional.ofNullable(prices.get(date));
    }

    public int size() {
        return prices.size();
    }
}
