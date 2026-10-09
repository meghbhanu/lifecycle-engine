package io.github.meghbhanu.lifecycle.payoff;

import io.github.meghbhanu.lifecycle.domain.Money;

public sealed interface ObservationOutcome permits Continues, Missed, Redeemed {
    Money coupon();
}
