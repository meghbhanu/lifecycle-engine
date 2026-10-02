package io.github.meghbhanu.lifecycle.domain;

public enum ObservationFrequency {
    MONTHLY(1),
    QUARTERLY(3),
    SEMI_ANNUAL(6),
    ANNUAL(12);

    private final int months;

    ObservationFrequency(int months) {
        this.months = months;
    }

    public int months() {
        return this.months;
    }
}
