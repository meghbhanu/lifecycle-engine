package io.github.meghbhanu.lifecycle.schedule;

import java.time.LocalDate;
import java.util.Objects;

public record ScheduledDate(LocalDate unadjusted, LocalDate adjusted) {
    public ScheduledDate {
        Objects.requireNonNull(unadjusted, "unadjusted");
        Objects.requireNonNull(adjusted, "adjusted");
    }
}
