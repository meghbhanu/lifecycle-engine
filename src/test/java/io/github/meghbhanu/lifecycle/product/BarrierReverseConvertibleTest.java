package io.github.meghbhanu.lifecycle.product;

import io.github.meghbhanu.lifecycle.domain.Percentage;
import io.github.meghbhanu.lifecycle.domain.TestTerms;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BarrierReverseConvertibleTest {

    @Test
    void exactly100PercentBarrierThrows() {
        Percentage capital = Percentage.ofPercent("100");
        assertThrows(IllegalArgumentException.class, () -> barrierReverseConvertible(capital));
    }

    @Test
    void barrierJustBelow100PercentIsAccepted() {
        Percentage capital = Percentage.ofPercent("99");
        assertEquals(Percentage.ofPercent("99"), barrierReverseConvertible(capital).capitalBarrier());
    }

    private BarrierReverseConvertible barrierReverseConvertible(Percentage capital) {
        return new BarrierReverseConvertible(TestTerms.common("BRC-001"),
                capital);
    }
}
