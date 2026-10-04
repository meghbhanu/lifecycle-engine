package io.github.meghbhanu.lifecycle.product;

import io.github.meghbhanu.lifecycle.domain.Percentage;
import io.github.meghbhanu.lifecycle.domain.TestTerms;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductDescriptionsTest {

    @Test
    void autocallableDescriptionListsAllThreeBarriers() {
        Percentage capital      = Percentage.ofPercent("60");
        Percentage coupon       = Percentage.ofPercent("70");
        Percentage autocall     = Percentage.ofPercent("100");
        Autocallable autocallable = new Autocallable(TestTerms.common("AC-001"),
                coupon, autocall, capital);

        String description = "Autocallable AC-001: autocall at 100%, coupon barrier 70%, capital barrier 60%";

        assertEquals(description, ProductDescriptions.describe(autocallable));
    }

    @Test
    void barrierReverseConvertibleDescriptionListsAllCapitalBarrier() {
        Percentage capital      = Percentage.ofPercent("60");
        BarrierReverseConvertible barrierReverseConvertible =
                new BarrierReverseConvertible(TestTerms.common("BRC-001"), capital);

        String description = "BarrierReverseConvertible BRC-001: capital barrier 60%";

        assertEquals(description, ProductDescriptions.describe(barrierReverseConvertible));
    }

    @Test
    void fixedCouponNoteDescription() {
        FixedCouponNote fixedCouponNote =
                new FixedCouponNote(TestTerms.common("FCN-001"));

        String description = "FixedCouponNote FCN-001";

        assertEquals(description, ProductDescriptions.describe(fixedCouponNote));
    }
}
