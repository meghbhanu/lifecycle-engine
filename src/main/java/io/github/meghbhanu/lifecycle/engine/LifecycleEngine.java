package io.github.meghbhanu.lifecycle.engine;

import io.github.meghbhanu.lifecycle.domain.CommonTerms;
import io.github.meghbhanu.lifecycle.domain.Currency;
import io.github.meghbhanu.lifecycle.domain.Money;
import io.github.meghbhanu.lifecycle.marketdata.PriceSource;
import io.github.meghbhanu.lifecycle.payoff.*;
import io.github.meghbhanu.lifecycle.product.Autocallable;
import io.github.meghbhanu.lifecycle.product.Product;
import io.github.meghbhanu.lifecycle.schedule.ScheduledDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class LifecycleEngine {

    private static final Logger log = LoggerFactory.getLogger(LifecycleEngine.class);
    private final PayoffCalculator calculator;

    public LifecycleEngine(PayoffCalculator calculator) {
        Objects.requireNonNull(calculator, "calculator");
        this.calculator = calculator;
    }

    public List<LifecycleEvent> run(
            Product product, List<ScheduledDate> schedule, PriceSource prices) {
        Objects.requireNonNull(product, "product");
        Objects.requireNonNull(schedule, "schedule");
        Objects.requireNonNull(prices, "prices");

        List<LifecycleEvent> events = new ArrayList<>();
        CommonTerms terms = product.terms();
        String id = terms.productId();
        Currency currency = terms.notional().currency();
        boolean memory = product instanceof Autocallable a && a.memoryCoupon();
        Money missed = Money.of("0", terms.notional().currency());

        log.info("Running {} over {} observation dates", id, schedule.size());

        for (int i = 0; i < schedule.size(); i++) {
            LocalDate date = schedule.get(i).adjusted();
            boolean isFinal = i == schedule.size() - 1;
            Optional<Money> price = prices.priceOn(date);
            if (price.isEmpty()) {
                events.add(new PriceMissing(id, date));
                log.warn("No price for {} on {}; observation skipped", id, date);
                continue;
            }

            ObservationOutcome outcome = calculator.evaluate(
                    product, new Observation(date, price.orElseThrow(), isFinal));
            log.debug("{} on {}: {}", id, date, outcome);

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
                    log.info("{} redeemed on {} ({})", id, date, r.reason());
                    return List.copyOf(events);
                }
            }
        }
        return List.copyOf(events);
    }
}
