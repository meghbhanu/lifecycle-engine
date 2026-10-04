package io.github.meghbhanu.lifecycle.product;

import io.github.meghbhanu.lifecycle.domain.Percentage;
import io.github.meghbhanu.lifecycle.domain.TestTerms;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AutocallableTest {

    private Percentage capital;
    private Percentage coupon;
    private Percentage autocall;

    @BeforeEach
    void setup() {
        capital      = Percentage.ofPercent("60");
        coupon       = Percentage.ofPercent("70");
        autocall     = Percentage.ofPercent("100");
    }

    @Test
    void validAutocallableBuilds() {
        Autocallable product = autocallable();

        assertAll(
                () -> assertEquals(Percentage.ofPercent("70"), product.couponBarrier()),
                () -> assertEquals(Percentage.ofPercent("100"), product.autocallTrigger()),
                () -> assertEquals(Percentage.ofPercent("60"), product.capitalBarrier())
        );
    }

    @Test
    void capitalBarrierAboveCouponBarrierThrows() {
        capital = Percentage.ofPercent("75");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, this::autocallable);
        assertTrue(ex.getMessage().contains("capital barrier"));
    }

    @Test
    void couponBarrierAboveAutocallTriggerThrows() {
        coupon = Percentage.ofPercent("120");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, this::autocallable);
        assertTrue(ex.getMessage().contains("autocall"));
    }

    private Autocallable autocallable() {
        return new Autocallable(TestTerms.common("AC-001"), coupon, autocall, capital);
    }
}
