package io.github.meghbhanu.lifecycle.schedule;

import java.time.DayOfWeek;
import java.util.*;
import java.time.LocalDate;

public final class HolidayCalendar {

    private final String name;
    private final NavigableSet<LocalDate> holidays;

    public HolidayCalendar(String name, Collection<LocalDate> holidays) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(holidays, "holidays");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        this.name = name;
        this.holidays = Collections.unmodifiableNavigableSet(
                new TreeSet<>(holidays));
    }

    public String name() {
        return name;
    }

    public boolean isHoliday(LocalDate date) {
        return holidays.contains(date);
    }

    public boolean isBusinessDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY
                && day != DayOfWeek.SUNDAY
                && !isHoliday(date);
    }

    public LocalDate adjust(LocalDate date, RollConvention convention) {
        return switch (convention) {
            case FOLLOWING -> rollForward(date);
            case PRECEDING -> rollBackward(date);
            case MODIFIED_FOLLOWING -> {
                LocalDate following = rollForward(date);
                yield following.getMonth() == date.getMonth() ? following : rollBackward(date);
            }
        };
    }

    private LocalDate rollForward(LocalDate date) {
        LocalDate candidate = date;
        while (!isBusinessDay(candidate)) {
            candidate = candidate.plusDays(1);
        }
        return candidate;
    }

    private LocalDate rollBackward(LocalDate date) {
        LocalDate candidate = date;
        while (!isBusinessDay(candidate)) {
            candidate = candidate.minusDays(1);
        }
        return candidate;
    }

    public Optional<LocalDate> nextHolidayAfter(LocalDate date) {
        return Optional.ofNullable(holidays.higher(date));
    }
}
