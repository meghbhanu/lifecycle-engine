package io.github.meghbhanu.lifecycle.payoff;

import io.github.meghbhanu.lifecycle.domain.Money;

public record Continues(Money coupon) implements ObservationOutcome {
}
