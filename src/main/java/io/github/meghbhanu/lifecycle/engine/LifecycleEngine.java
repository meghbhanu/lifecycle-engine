package io.github.meghbhanu.lifecycle.engine;

import io.github.meghbhanu.lifecycle.domain.CommonTerms;
import io.github.meghbhanu.lifecycle.domain.Currency;
import io.github.meghbhanu.lifecycle.domain.Money;
import io.github.meghbhanu.lifecycle.marketdata.PriceHistory;
import io.github.meghbhanu.lifecycle.payoff.*;
import io.github.meghbhanu.lifecycle.product.Autocallable;
import io.github.meghbhanu.lifecycle.product.Product;
import io.github.meghbhanu.lifecycle.schedule.ScheduledDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class LifecycleEngine {

    private final PayoffCalculator calculator;

    public LifecycleEngine(PayoffCalculator calculator) {
        Objects.requireNonNull(calculator, "calculator");
        this.calculator = calculator;
    }

    public List<LifecycleEvent> run(
            Product product, List<ScheduledDate> schedule, PriceHistory prices) {
        Objects.requireNonNull(product, "product");
        Objects.requireNonNull(schedule, "schedule");
        Objects.requireNonNull(prices, "prices");

        List<LifecycleEvent> events = new ArrayList<>();
        CommonTerms terms = product.terms();
        String id = terms.productId();
        Currency currency = terms.notional().currency();
        boolean memory = product instanceof Autocallable a && a.memoryCoupon();

        Money missed = Money.of("0", terms.notional().currency());

        for (int i = 0; i < schedule.size(); i++) {
            LocalDate date = schedule.get(i).adjusted();
            boolean isFinal = i == schedule.size() - 1;
            Optional<Money> price = prices.priceOn(date);
            if (price.isEmpty()) {
                events.add(new PriceMissing(id, date));
                continue;
            }

            ObservationOutcome outcome = calculator.evaluate(
                    product, new Observation(date, price.orElseThrow(), isFinal));
            switch (outcome) {
                case Continues c -> {
                    events.add(new CouponPaid(id, date, c.coupon().plus(missed)));
                    missed = Money.of("0", terms.notional().currency());
                }
                case Missed m -> {
                    events.add(new CouponMissed(id, date, m.coupon()));
                    if (memory) {
                        missed = missed.plus(m.coupon());
                    }
                }
                case Redeemed r -> {
                    if (r.coupon().amount().signum() > 0) {
                        events.add(new CouponPaid(id, date, r.coupon().plus(missed)));
                    }
                    events.add(switch (r.reason()) {
                        case AUTOCALL -> new Autocalled(id, date, r.redemption());
                        case MATURITY -> new Matured(id, date, r.redemption());
                    });
                    return List.copyOf(events);
                }
            }
        }
        return List.copyOf(events);
    }
}
