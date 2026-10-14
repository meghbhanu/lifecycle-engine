package io.github.meghbhanu.lifecycle.reports;

import io.github.meghbhanu.lifecycle.domain.Money;
import io.github.meghbhanu.lifecycle.engine.*;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public final class LifecycleReports {

    private LifecycleReports() {}

    public static Set<String> liveProducts(List<LifecycleEvent> events) {
        Objects.requireNonNull(events, "events");

        Set<String> all = events.stream()
                .map(LifecycleEvent::productId)
                .collect(Collectors.toSet());

        Set<String> redeemed = events.stream()
                .filter(e -> e instanceof Autocalled || e instanceof Matured)
                .map(LifecycleEvent::productId)
                .collect(Collectors.toSet());

        Set<String> live = new HashSet<>(all);
        live.removeAll(redeemed);
        return live;
    }

    public static Map<String, List<LifecycleEvent>> timeline(List<LifecycleEvent> events) {
        return events.stream()
                .collect(Collectors.groupingBy(
                        LifecycleEvent::productId,
                        TreeMap::new,
                        Collectors.toList()));
    }

    public static Map<YearMonth, Money> cashFlowsByMonth(List<LifecycleEvent> events) {
        return events.stream()
                .filter(LifecycleReports::isCashFlow)
                .collect(Collectors.toMap(
                        e -> YearMonth.from(e.date()),
                        LifecycleReports::cashAmount,
                        Money::plus,
                        TreeMap::new
                ));
    }

    private static Money cashAmount(LifecycleEvent event) {
        return switch (event) {
            case CouponPaid p -> p.amount();
            case Autocalled a -> a.redemption();
            case Matured m -> m.redemption();
            case CouponMissed c -> throw new IllegalArgumentException("not a cash flow: " + c);
            case PriceMissing p -> throw new IllegalArgumentException("not a cash flow: " + p);
        };
    }

    private static boolean isCashFlow(LifecycleEvent e) {
        return e instanceof CouponPaid || e instanceof Autocalled ||
                e instanceof Matured;
    }
}
