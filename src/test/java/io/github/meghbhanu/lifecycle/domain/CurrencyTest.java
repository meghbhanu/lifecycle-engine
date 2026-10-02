package io.github.meghbhanu.lifecycle.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrencyTest {

    @ParameterizedTest
    @CsvSource({
            "USD, 2",
            "EUR, 2",
            "GBP, 2",
            "CHF, 2",
            "JPY, 0"
    })
    void decimalPlacesMatchMarketConvention(Currency currency, int expected) {
        assertEquals(expected, currency.decimalPlaces());
    }
}