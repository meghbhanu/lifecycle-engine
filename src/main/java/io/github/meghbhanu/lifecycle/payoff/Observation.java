package io.github.meghbhanu.lifecycle.payoff;

import io.github.meghbhanu.lifecycle.domain.Money;

import java.time.LocalDate;

public record Observation(LocalDate date, Money price, boolean finalObservation) {

}
