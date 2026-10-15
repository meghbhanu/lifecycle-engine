package io.github.meghbhanu.lifecycle.marketdata;

import io.github.meghbhanu.lifecycle.domain.Money;

import java.time.LocalDate;
import java.util.Optional;

public interface PriceSource {
    Optional<Money> priceOn(LocalDate date);
}
