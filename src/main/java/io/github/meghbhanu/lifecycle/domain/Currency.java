package io.github.meghbhanu.lifecycle.domain;

public enum Currency {
    USD(2),
    EUR(2),
    GBP(2),
    CHF(2),
    JPY(0);

    private final int decimalPlaces;

    Currency(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public int decimalPlaces() {
        return decimalPlaces;
    }
}
