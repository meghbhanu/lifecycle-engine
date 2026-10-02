package io.github.meghbhanu.lifecycle.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProductTermsTest {

    private Underlying underlying;
    private Money notional;
    private Money initialPrice;
    private Percentage capital;
    private Percentage coupon;
    private Percentage autocall;
    private LocalDate issueDate;
    private LocalDate maturityDate;

    @BeforeEach
    void setup() {
        underlying   = new Underlying("AAPL", Currency.USD);
        notional     = Money.of("1000000", Currency.USD);
        initialPrice = Money.of("200", Currency.USD);
        capital      = Percentage.ofPercent("60");
        coupon       = Percentage.ofPercent("70");
        autocall     = Percentage.ofPercent("100");
        issueDate    = LocalDate.of(2026, 10, 2);
        maturityDate = issueDate.plusYears(3);
    }

    @Test
    void validTermsBuild() {
        assertDoesNotThrow(this::terms);
    }

    @Test
    void maturityOnIssueDateThrows() {
        maturityDate = issueDate;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, this::terms);
        assertTrue(ex.getMessage().contains("maturity"));
    }

    @Test
    void capitalBarrierAboveCouponBarrierThrows() {
        capital = Percentage.ofPercent("75");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, this::terms);
        assertTrue(ex.getMessage().contains("capital barrier"));
    }

    @Test
    void couponBarrierAboveAutocallTriggerThrows() {
        coupon = Percentage.ofPercent("120");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, this::terms);
        assertTrue(ex.getMessage().contains("coupon barrier"));
    }

    @Test
    void zeroNotionalThrows() {
        notional = Money.of("0", Currency.USD);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, this::terms);
        assertTrue(ex.getMessage().contains("notional"));
    }

    @Test
    void initialPriceCurrencyMismatchThrows() {
        initialPrice = Money.of("200", Currency.EUR);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, this::terms);
        assertTrue(ex.getMessage().contains("initial price"));
    }

    private ProductTerms terms() {
        return new ProductTerms("AC-001", underlying, notional, initialPrice,
                Percentage.ofPercent("8"), coupon, autocall, capital,
                issueDate, maturityDate, ObservationFrequency.QUARTERLY);
    }
}
