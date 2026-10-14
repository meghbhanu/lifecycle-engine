package io.github.meghbhanu.lifecycle.reports;

import io.github.meghbhanu.lifecycle.domain.Money;
import io.github.meghbhanu.lifecycle.engine.Autocalled;
import io.github.meghbhanu.lifecycle.engine.CouponMissed;
import io.github.meghbhanu.lifecycle.engine.CouponPaid;
import io.github.meghbhanu.lifecycle.engine.LifecycleEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.meghbhanu.lifecycle.domain.Currency.USD;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LifecycleReportsTest {

    private static final String AUTOCALLED_ID = "AC-001";
    private static final String LIVE_ID = "AC-002";

    private static final LocalDate JAN = LocalDate.of(2027, 1, 4);
    private static final LocalDate APR = LocalDate.of(2027, 4, 2);

    private static final Money COUPON = Money.of("20000", USD);
    private static final Money NOTIONAL = Money.of("1000000", USD);

    // AC-001: paid Jan, paid Apr, autocalled Apr. AC-002: paid Jan, missed Apr (still live)
    private static final List<LifecycleEvent> EVENTS = List.of(
            new CouponPaid(AUTOCALLED_ID, JAN, COUPON),
            new CouponPaid(LIVE_ID, JAN, COUPON),
            new CouponPaid(AUTOCALLED_ID, APR, COUPON),
            new Autocalled(AUTOCALLED_ID, APR, NOTIONAL),
            new CouponMissed(LIVE_ID, APR, COUPON));

    @Test
    void reportsSummariseEventsAcrossProducts() {
        Map<String, List<LifecycleEvent>> timeline = LifecycleReports.timeline(EVENTS);

        assertAll(
                // only AC-002 has no Autocalled/Matured event
                () -> assertEquals(Set.of(LIVE_ID), LifecycleReports.liveProducts(EVENTS)),

                // product IDs come out sorted (TreeMap)
                () -> assertEquals(List.of(AUTOCALLED_ID, LIVE_ID), new ArrayList<>(timeline.keySet())),

                // each product's events keep their input order
                () -> assertEquals(List.of(
                                new CouponPaid(AUTOCALLED_ID, JAN, COUPON),
                                new CouponPaid(AUTOCALLED_ID, APR, COUPON),
                                new Autocalled(AUTOCALLED_ID, APR, NOTIONAL)),
                        timeline.get(AUTOCALLED_ID)),

                // Jan: two coupons merged by Money::plus
                // Apr: coupon + redemption; the missed coupon is not cash, so excluded
                () -> assertEquals(Map.of(
                                YearMonth.of(2027, 1), Money.of("40000", USD),
                                YearMonth.of(2027, 4), Money.of("1020000", USD)),
                        LifecycleReports.cashFlowsByMonth(EVENTS)));
    }
}
