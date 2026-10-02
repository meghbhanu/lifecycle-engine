package io.github.meghbhanu.lifecycle.domain;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PercentageTest {

    @Test
    void applyToReturnsThatShareOfMoney() {
        assertEquals(Money.of("120", Currency.USD),
                Percentage.ofPercent("60").applyTo(Money.of("200", Currency.USD)));
    }

    @Test
    void ofPercentKeepsFractionalPercentages() {
        assertEquals(new BigDecimal("0.0725"), Percentage.ofPercent("7.25").fraction());
    }

    @Test
    void negativePercentValueIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Percentage(new BigDecimal("-1")));
    }

    @Test
    void zeroPercentageIsValid() {
        assertEquals(Money.of("0", Currency.USD),
                Percentage.ofPercent("0").applyTo(Money.of("200", Currency.USD)));
    }

    @Test
    void toStringOfPercentagePrintsNeatly() {
        assertEquals("1000%", Percentage.ofPercent("1000").toString());
        assertEquals("7.25%", Percentage.ofPercent("7.25").toString());
    }
}
