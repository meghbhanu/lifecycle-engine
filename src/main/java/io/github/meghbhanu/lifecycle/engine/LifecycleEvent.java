package io.github.meghbhanu.lifecycle.engine;

import java.time.LocalDate;

public sealed interface LifecycleEvent
        permits CouponPaid, CouponMissed, Autocalled, Matured, PriceMissing {
    String productId();
    LocalDate date();
}
