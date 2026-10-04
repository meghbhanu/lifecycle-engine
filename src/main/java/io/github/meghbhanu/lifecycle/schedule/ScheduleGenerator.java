package io.github.meghbhanu.lifecycle.schedule;

import io.github.meghbhanu.lifecycle.domain.ObservationFrequency;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ScheduleGenerator {

    private final HolidayCalendar calendar;
    private final RollConvention convention;

    public ScheduleGenerator(HolidayCalendar calendar, RollConvention convention) {
        Objects.requireNonNull(calendar, "calendar");
        Objects.requireNonNull(convention, "convention");

        this.calendar = calendar;
        this.convention = convention;
    }

    public List<ScheduledDate> generate(LocalDate start, LocalDate end, ObservationFrequency frequency) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        Objects.requireNonNull(frequency, "frequency");

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("end date must be after start date");
        }

        List<ScheduledDate> schedule = new ArrayList<>();

        for (int period = 1; ; period++) {
            LocalDate unadjusted = start.plusMonths((long) period * frequency.months());
            if (unadjusted.isAfter(end)) {
                break;
            }
            schedule.add(new ScheduledDate(unadjusted, calendar.adjust(unadjusted, convention)));
        }

        if (schedule.isEmpty() || !schedule.getLast().unadjusted().equals(end)) {
            throw new IllegalArgumentException(
                    "end %s does not fall on the %s schedule from %s".formatted(end, frequency, start));
        }

        return List.copyOf(schedule);
    }
}
