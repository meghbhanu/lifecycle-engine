package io.github.meghbhanu.lifecycle.domain;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    @Test
    void amountsDifferingOnlyInScaleAreEqual() {
        Money a = Money.of("23.0", Currency.USD);
        Money b = Money.of("23.00", Currency.USD);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void plusAddsSameCurrencyAmounts() {
        Money m1 = Money.of("25", Currency.USD);
        Money m2 = Money.of("23.00", Currency.USD);
        assertEquals(Money.of("48.00", Currency.USD), m1.plus(m2));
    }

    @Test
    void plusRejectsDifferentCurrencies() {
        Money m1 = Money.of("25", Currency.JPY);
        Money m2 = Money.of("23.00", Currency.USD);
        assertThrows(IllegalArgumentException.class, () -> m1.plus(m2));
    }

    @Test
    void moneyOfJPYCurrencyIsRoundedToZeroDecimalPlaces() {
        assertEquals(new BigDecimal("25"), Money.of("25.4", Currency.JPY).amount());
    }
}
