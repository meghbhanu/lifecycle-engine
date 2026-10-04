package io.github.meghbhanu.lifecycle.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HolidayCalendarTest {

    private final LocalDate dec25 = LocalDate.of(2026, 12, 25);
    private final LocalDate jan1 = LocalDate.of(2027, 1, 1);
    private final LocalDate may25 = LocalDate.of(2026, 5, 25);
    private final HolidayCalendar calendar = new HolidayCalendar("Random Holidays",
            List.of(dec25, jan1, may25));

    @Test
    void normalWeekdayIsBusinessDay() {
        assertTrue(calendar.isBusinessDay(LocalDate.of(2026,12,23)));
    }

    @Test
    void saturdayIsNotABusinessDay() {
        assertFalse(calendar.isBusinessDay(LocalDate.of(2026,12,26)));
    }

    @Test
    void holidayIsNotABusinessDay() {
        assertFalse(calendar.isBusinessDay(LocalDate.of(2026,12,25)));
    }

    @ParameterizedTest
    @EnumSource(RollConvention.class)
    void businessDayIsUnchangedUnderEveryConvention(RollConvention convention) {
        LocalDate wednesday = LocalDate.of(2026, 12, 23);
        assertEquals(wednesday, calendar.adjust(wednesday, convention));
    }

    @Test
    void followingSkipsHolidayFridayAndWeekend() {
        LocalDate dec28 = LocalDate.of(2026,12,28);
        assertEquals(dec28, calendar.adjust(dec25, RollConvention.FOLLOWING));
    }

    @Test
    void followingSkipsWeekendAndHolidayMonday() {
        LocalDate may23 = LocalDate.of(2026,5,23);
        LocalDate may26 = LocalDate.of(2026,5,26);
        assertEquals(may26, calendar.adjust(may23, RollConvention.FOLLOWING));
    }

    @Test
    void precedingReturnsPreviousBusinessDay() {
        LocalDate dec24 = LocalDate.of(2026,12,24);
        assertEquals(dec24, calendar.adjust(dec25, RollConvention.PRECEDING));
    }

    @Test
    void modifiedFollowingAcrossMonthEndReturnsPreviousBusinessDay() {
        LocalDate jan30 = LocalDate.of(2027,1,30);
        LocalDate jan29 = LocalDate.of(2027,1,29);
        assertEquals(jan29, calendar.adjust(jan30, RollConvention.MODIFIED_FOLLOWING));
    }

    @Test
    void modifiedFollowingWithNoCrossingReturnsNextBusinessDay() {
        LocalDate dec28 = LocalDate.of(2026,12,28);
        assertEquals(dec28, calendar.adjust(dec25, RollConvention.MODIFIED_FOLLOWING));
    }

    @Test
    void nextHolidayReturnedAfterDec25() {
        LocalDate jan1 = LocalDate.of(2027, 1, 1);
        assertEquals(Optional.of(jan1), calendar.nextHolidayAfter(dec25));
    }

    @Test
    void nextHolidayEmptyAfterJan1() {
        assertEquals(Optional.empty(), calendar.nextHolidayAfter(jan1));
    }

    @Test
    void changingTheInputListDoesNotChangeTheCalendar() {
        List<LocalDate> input = new ArrayList<>(List.of(dec25));
        HolidayCalendar calendar = new HolidayCalendar("Test", input);

        LocalDate dec23 = LocalDate.of(2026, 12, 23);
        input.add(dec23);                                  // the caller changes their list afterwards

        assertFalse(calendar.isHoliday(dec23));            // the calendar is unaffected
        assertTrue(calendar.isBusinessDay(dec23));
    }
}
