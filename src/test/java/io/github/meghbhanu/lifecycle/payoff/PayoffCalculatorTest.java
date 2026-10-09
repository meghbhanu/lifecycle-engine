package io.github.meghbhanu.lifecycle.payoff;

import io.github.meghbhanu.lifecycle.domain.Money;
import io.github.meghbhanu.lifecycle.domain.Percentage;
import io.github.meghbhanu.lifecycle.domain.TestTerms;
import io.github.meghbhanu.lifecycle.product.Autocallable;
import io.github.meghbhanu.lifecycle.product.BarrierReverseConvertible;
import io.github.meghbhanu.lifecycle.product.FixedCouponNote;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.github.meghbhanu.lifecycle.domain.Currency.EUR;
import static io.github.meghbhanu.lifecycle.domain.Currency.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PayoffCalculatorTest {

    private static final LocalDate OBSERVATION_DATE = LocalDate.of(2027, 1, 4);
    private static final Money NOTIONAL = Money.of("1000000", USD);
    private static final Money COUPON = Money.of("20000", USD);
    private static final Money NO_COUPON = Money.of("0", USD);

    private final PayoffCalculator calculator = new PayoffCalculator();

    private static Observation observation(String price, boolean finalObservation) {
        return new Observation(OBSERVATION_DATE, Money.of(price, USD), finalObservation);
    }

    @Nested
    class AutocallableProduct {

        // autocall at 100% (200), coupon barrier 70% (140), capital barrier 60% (120)
        private final Autocallable autocallable = new Autocallable(
                TestTerms.common("AC-001"),
                Percentage.ofPercent("70"),
                Percentage.ofPercent("100"),
                Percentage.ofPercent("60"),
                false);

        @Test
        void priceAboveTriggerAutocallsWithCoupon() {
            assertEquals(new Redeemed(COUPON, NOTIONAL, RedemptionReason.AUTOCALL),
                    calculator.evaluate(autocallable, observation("210", false)));
        }

        @Test
        void priceExactlyAtTriggerAutocalls() {
            assertEquals(new Redeemed(COUPON, NOTIONAL, RedemptionReason.AUTOCALL),
                    calculator.evaluate(autocallable, observation("200", false)));
        }

        @Test
        void priceBetweenCouponBarrierAndTriggerPaysCouponAndContinues() {
            assertEquals(new Continues(COUPON),
                    calculator.evaluate(autocallable, observation("150", false)));
        }

        @Test
        void priceBelowCouponBarrierMissesCoupon() {
            assertEquals(new Missed(COUPON),
                    calculator.evaluate(autocallable, observation("130", false)));
        }

        @Test
        void finalAboveCouponBarrierPaysCouponAndFullNotional() {
            assertEquals(new Redeemed(COUPON, NOTIONAL, RedemptionReason.MATURITY),
                    calculator.evaluate(autocallable, observation("160", true)));   // 80%
        }

        @Test
        void finalAboveCapitalBarrierReturnsFullNotionalWithoutCoupon() {
            assertEquals(new Redeemed(NO_COUPON, NOTIONAL, RedemptionReason.MATURITY),
                    calculator.evaluate(autocallable, observation("130", true)));
        }

        @Test
        void finalExactlyAtCapitalBarrierReturnsFullNotional() {
            assertEquals(new Redeemed(NO_COUPON, NOTIONAL, RedemptionReason.MATURITY),
                    calculator.evaluate(autocallable, observation("120", true)));
        }

        @Test
        void finalBelowCapitalBarrierLosesCapitalWithPerformance() {
            assertEquals(new Redeemed(NO_COUPON, Money.of("500000", USD), RedemptionReason.MATURITY),
                    calculator.evaluate(autocallable, observation("100", true)));
        }
    }

    @Nested
    class BarrierReverseConvertibleProduct {

        // capital barrier 60% (120)
        private final BarrierReverseConvertible brc = new BarrierReverseConvertible(
                TestTerms.common("BRC-001"),
                Percentage.ofPercent("60"));

        @Test
        void paysCouponEvenWhenPriceHasFallen() {
            assertEquals(new Continues(COUPON),
                    calculator.evaluate(brc, observation("100", false)));
        }

        @Test
        void finalBelowCapitalBarrierPaysCouponAndReducedRedemption() {
            assertEquals(new Redeemed(COUPON, Money.of("500000", USD), RedemptionReason.MATURITY),
                    calculator.evaluate(brc, observation("100", true)));
        }
    }

    @Nested
    class FixedCouponNoteProduct {

        private final FixedCouponNote note = new FixedCouponNote(TestTerms.common("FCN-001"));

        @Test
        void paysCouponRegardlessOfPrice() {
            assertEquals(new Continues(COUPON),
                    calculator.evaluate(note, observation("50", false)));
        }

        @Test
        void finalReturnsFullNotionalEvenAfterPriceFall() {
            assertEquals(new Redeemed(COUPON, NOTIONAL, RedemptionReason.MATURITY),
                    calculator.evaluate(note, observation("100", true)));
        }
    }

    @Test
    void priceInDifferentCurrencyFromUnderlyingThrows() {
        FixedCouponNote note = new FixedCouponNote(TestTerms.common("FCN-001"));
        Observation eurPrice = new Observation(OBSERVATION_DATE, Money.of("200", EUR), false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calculator.evaluate(note, eurPrice));
        assertTrue(ex.getMessage().contains("does not match"));
    }
}
