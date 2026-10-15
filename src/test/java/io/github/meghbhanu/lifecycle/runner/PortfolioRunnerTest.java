package io.github.meghbhanu.lifecycle.runner;

import io.github.meghbhanu.lifecycle.domain.Money;
import io.github.meghbhanu.lifecycle.domain.Percentage;
import io.github.meghbhanu.lifecycle.domain.TestTerms;
import io.github.meghbhanu.lifecycle.engine.LifecycleEngine;
import io.github.meghbhanu.lifecycle.engine.LifecycleEvent;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.meghbhanu.lifecycle.domain.Currency.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PortfolioRunnerTest {

    private static final LocalDate Q1 = LocalDate.of(2027, 1, 4);
    private static final LocalDate Q2 = LocalDate.of(2027, 4, 2);
    private static final LocalDate Q3 = LocalDate.of(2027, 7, 2);
    private static final LocalDate Q4 = LocalDate.of(2027, 10, 4);
    private static final LocalDate Q5 = LocalDate.of(2028, 1, 3);

    private static final List<ScheduledDate> SCHEDULE = schedule(Q1, Q2, Q3, Q4, Q5);
    private static final PriceHistory PRICES = prices(Map.of(Q1, "180", Q2, "130", Q3, "125",
            Q4, "160", Q5, "205"));

    private final List<ProductRun> runs = List.of(
            new ProductRun(autocallable("AC-001", true), SCHEDULE, PRICES),
            new ProductRun(autocallable("AC-002", false), SCHEDULE, PRICES),
            new ProductRun(autocallable("AC-003", true), SCHEDULE, PRICES));

    private final LifecycleEngine engine = new LifecycleEngine(new PayoffCalculator());

    @Test
    void runningSerialAndParallelReturnsTheSame() {
        try (ExecutorService pool = Executors.newFixedThreadPool(4)) {
            PortfolioRunner runner = new PortfolioRunner(engine, pool);
            List<LifecycleEvent> serial = runner.runSerial(runs);
            assertFalse(serial.isEmpty());
            assertEquals(serial, runner.runParallel(runs));
        }
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

    private static Autocallable autocallable(String id, boolean memory) {
        return new Autocallable(TestTerms.common(id), Percentage.ofPercent("70"),
                Percentage.ofPercent("100"), Percentage.ofPercent("60"), memory);
    }
}
