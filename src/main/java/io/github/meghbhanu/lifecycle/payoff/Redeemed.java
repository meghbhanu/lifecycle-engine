package io.github.meghbhanu.lifecycle.payoff;

import io.github.meghbhanu.lifecycle.domain.Money;

public record Redeemed(Money coupon, Money redemption, RedemptionReason reason)
        implements ObservationOutcome {
}
