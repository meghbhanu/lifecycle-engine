package io.github.meghbhanu.lifecycle.engine;

import io.github.meghbhanu.lifecycle.domain.Currency;
import io.github.meghbhanu.lifecycle.domain.Money;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LifecycleEventTest {

    private static final LocalDate DATE = LocalDate.of(2026, 10, 9);
    private static final String PRODUCT_ID = "AC-001";

    @Test
    void validCouponPaidBuilds() {
        CouponPaid couponPaid = new CouponPaid(PRODUCT_ID, DATE,
                Money.of("10", Currency.USD));

        assertAll(
                () -> assertEquals(Money.of("10", Currency.USD), couponPaid.amount()),
                () -> assertEquals(PRODUCT_ID, couponPaid.productId()),
                () -> assertEquals(DATE, couponPaid.date())
        );
    }

    @Test
    void zeroAmountForMissedCouponThrows() {
        assertThrows(IllegalArgumentException.class, () -> new CouponMissed(
                PRODUCT_ID, DATE, Money.of("0", Currency.USD)));
    }

    @Test
    void zeroRedemptionIsAllowedForMaturedProduct() {
        Matured matured = new Matured(PRODUCT_ID, DATE, Money.of("0", Currency.USD));
        assertEquals(Money.of("0", Currency.USD), matured.redemption());
    }

    @Test
    void blankProductIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> new CouponMissed(
                "", DATE, Money.of("20", Currency.USD)));
    }
}
