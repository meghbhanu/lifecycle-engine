package io.github.meghbhanu.lifecycle.schedule;

import io.github.meghbhanu.lifecycle.domain.ObservationFrequency;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleGeneratorTest {

    private final List<LocalDate> holidays = List.of(
            LocalDate.of(2026, 12,25),
            LocalDate.of(2027, 1,1),
            LocalDate.of(2026, 5,25)
    );
    private final HolidayCalendar calendar = new HolidayCalendar("holidays", holidays);
    private final ScheduleGenerator generator = new ScheduleGenerator(calendar, RollConvention.MODIFIED_FOLLOWING);

    @Test
    void quarterlyScheduleRollsWeekendDatesForward() {
        List<ScheduledDate> expected = List.of(
                new ScheduledDate(LocalDate.of(2027, 1, 2), LocalDate.of(2027, 1, 4)),
                new ScheduledDate(LocalDate.of(2027, 4, 2), LocalDate.of(2027, 4, 2)),
                new ScheduledDate(LocalDate.of(2027, 7, 2), LocalDate.of(2027, 7, 2)),
                new ScheduledDate(LocalDate.of(2027, 10, 2), LocalDate.of(2027, 10, 4))
        );

        LocalDate start = LocalDate.of(2026, 10, 2);
        LocalDate end = LocalDate.of(2027, 10, 2);

        assertEquals(expected, generator.generate(start, end, ObservationFrequency.QUARTERLY));
    }

    @Test
    void monthlyScheduleFromMonthEndDoesNotDrift() {
        List<ScheduledDate> expected = List.of(
                new ScheduledDate(LocalDate.of(2026, 2, 28), LocalDate.of(2026, 2, 27)),
                new ScheduledDate(LocalDate.of(2026, 3, 31), LocalDate.of(2026, 3, 31)),
                new ScheduledDate(LocalDate.of(2026, 4, 30), LocalDate.of(2026, 4, 30)),
                new ScheduledDate(LocalDate.of(2026, 5, 31), LocalDate.of(2026, 5, 29))
        );

        LocalDate start = LocalDate.of(2026, 1, 31);
        LocalDate end = LocalDate.of(2026, 5, 31);

        assertEquals(expected, generator.generate(start, end, ObservationFrequency.MONTHLY));
    }

    @Test
    void endDateThatDoesNotAlignWithFrequencyThrows() {
        LocalDate start = LocalDate.of(2026, 10, 2);
        LocalDate end = LocalDate.of(2027, 11, 15);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> generator.generate(start, end, ObservationFrequency.MONTHLY));
        assertTrue(ex.getMessage().contains("does not fall on"));
    }

    @Test
    void returnedScheduleListIsUnmodifiable() {
        LocalDate start = LocalDate.of(2026, 1, 31);
        LocalDate end = LocalDate.of(2026, 5, 31);

        List<ScheduledDate> schedule = generator.generate(start, end, ObservationFrequency.MONTHLY);
        assertThrows(UnsupportedOperationException.class, () -> schedule.add(
                new ScheduledDate(LocalDate.of(2026,12,12),
                        LocalDate.of(2026, 12,13))));
    }

    @Test
    void endDateBeforeTheFirstPeriod() {
        LocalDate start = LocalDate.of(2026, 1, 31);
        LocalDate end = LocalDate.of(2026, 2, 28);

        assertThrows(IllegalArgumentException.class,
                () -> generator.generate(start, end, ObservationFrequency.QUARTERLY));
    }

    @Test
    void endDateIsOnOrBeforeStart() {
        LocalDate start = LocalDate.of(2026, 1, 31);
        LocalDate end = LocalDate.of(2026, 1, 31);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> generator.generate(start, end, ObservationFrequency.QUARTERLY));
        assertTrue(ex.getMessage().contains("end date must be after start date"));
    }
}
