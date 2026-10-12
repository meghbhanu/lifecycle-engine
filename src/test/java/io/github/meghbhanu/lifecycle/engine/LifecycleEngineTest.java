package io.github.meghbhanu.lifecycle.engine;

import io.github.meghbhanu.lifecycle.domain.Money;
import io.github.meghbhanu.lifecycle.domain.Percentage;
import io.github.meghbhanu.lifecycle.domain.TestTerms;
import io.github.meghbhanu.lifecycle.marketdata.PriceHistory;
import io.github.meghbhanu.lifecycle.payoff.PayoffCalculator;
import io.github.meghbhanu.lifecycle.product.Autocallable;
import io.github.meghbhanu.lifecycle.schedule.ScheduledDate;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.meghbhanu.lifecycle.domain.Currency.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LifecycleEngineTest {

    private static final String ID = "AC-001";

    private static final LocalDate Q1 = LocalDate.of(2027, 1, 4);
    private static final LocalDate Q2 = LocalDate.of(2027, 4, 2);
    private static final LocalDate Q3 = LocalDate.of(2027, 7, 2);
    private static final LocalDate Q4 = LocalDate.of(2027, 10, 4);
    private static final LocalDate Q5 = LocalDate.of(2028, 1, 3);
    private static final LocalDate Q6 = LocalDate.of(2028, 4, 3);

    private static final Money COUPON = Money.of("20000", USD);
    private static final Money TWO_COUPONS = Money.of("40000", USD);
    private static final Money THREE_COUPONS = Money.of("60000", USD);
    private static final Money NOTIONAL = Money.of("1000000", USD);

    private static final boolean WITH_MEMORY = true;
    private static final boolean NO_MEMORY = false;

    private final PayoffCalculator calculator   = new PayoffCalculator();
    private final LifecycleEngine engine        = new LifecycleEngine(calculator);
    private static final Percentage CAPITAL_BARRIER = Percentage.ofPercent("60");
    private static final Percentage COUPON_BARRIER = Percentage.ofPercent("70");
    private static final Percentage AUTOCALL_TRIGGER = Percentage.ofPercent("100");

    @Test
    void memoryPaysBackMissedCoupons() {
        List<LifecycleEvent> events = engine.run(
                autocallable(WITH_MEMORY),
                schedule(Q1, Q2, Q3, Q4, Q5, Q6),
                prices(Map.of(Q1, "180", Q2, "130", Q3, "125",
                        Q4, "160", Q5, "205")));

        assertEquals(List.of(
                new CouponPaid(ID, Q1, COUPON),
                new CouponMissed(ID, Q2, COUPON),
                new CouponMissed(ID, Q3, COUPON),
                new CouponPaid(ID, Q4, THREE_COUPONS),
                new CouponPaid(ID, Q5, COUPON),
                new Autocalled(ID, Q5, NOTIONAL)), events);
    }

    @Test
    void missingPriceMidScheduleIsReportedAndLaterDatesStillEvaluated() {
        List<LifecycleEvent> events = engine.run(
                autocallable(NO_MEMORY),
                schedule(Q1, Q2, Q3),
                prices(Map.of(Q1, "180", Q3, "180")));

        assertEquals(List.of(
                new CouponPaid(ID, Q1, COUPON),
                new PriceMissing(ID, Q2),
                new CouponPaid(ID, Q3, COUPON),
                new Matured(ID, Q3, NOTIONAL)), events);
    }

    @Test
    void memoryPaidOnAutocallDate() {
        List<LifecycleEvent> events = engine.run(
                autocallable(WITH_MEMORY),
                schedule(Q1, Q2, Q3, Q4),
                prices(Map.of(Q1, "180", Q2, "130",
                        Q3, "205")));

        assertEquals(List.of(
                new CouponPaid(ID, Q1, COUPON),
                new CouponMissed(ID, Q2, COUPON),
                new CouponPaid(ID, Q3, TWO_COUPONS),
                new Autocalled(ID, Q3, NOTIONAL)), events);
    }

    @Test
    void couponPaymentsStopAfterAutocall() {
        List<LifecycleEvent> events = engine.run(
                autocallable(NO_MEMORY),
                schedule(Q1, Q2, Q3, Q4),
                prices(Map.of(Q1, "180", Q2, "205",
                        Q3, "180", Q4, "180")));

        assertEquals(List.of(
                new CouponPaid(ID, Q1, COUPON),
                new CouponPaid(ID, Q2, COUPON),
                new Autocalled(ID, Q2, NOTIONAL)), events);
    }



    private Autocallable autocallable(boolean memory) {
        return new Autocallable(TestTerms.common(ID), COUPON_BARRIER, AUTOCALL_TRIGGER, CAPITAL_BARRIER, memory);
    }

    private static List<ScheduledDate> schedule(LocalDate... dates) {
        List<ScheduledDate> result = new ArrayList<>();
        for (LocalDate date : dates) {
            result.add(new ScheduledDate(date, date));
        }
        return result;
    }

    private static PriceHistory prices(Map<LocalDate, String> pricesByDate) {
        Map<LocalDate, Money> money = new HashMap<>();
        for (Map.Entry<LocalDate, String> entry : pricesByDate.entrySet()) {
            money.put(entry.getKey(), Money.of(entry.getValue(), USD));
        }
        return new PriceHistory(money);
    }


}
