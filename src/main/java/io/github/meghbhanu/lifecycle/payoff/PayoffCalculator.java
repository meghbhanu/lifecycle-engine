package io.github.meghbhanu.lifecycle.payoff;

import io.github.meghbhanu.lifecycle.domain.CommonTerms;
import io.github.meghbhanu.lifecycle.domain.Currency;
import io.github.meghbhanu.lifecycle.domain.Money;
import io.github.meghbhanu.lifecycle.product.Autocallable;
import io.github.meghbhanu.lifecycle.product.BarrierReverseConvertible;
import io.github.meghbhanu.lifecycle.product.FixedCouponNote;
import io.github.meghbhanu.lifecycle.product.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class PayoffCalculator {

    public ObservationOutcome evaluate(Product product, Observation observation) {
        Objects.requireNonNull(product, "product");
        Objects.requireNonNull(observation, "observation");

        Currency priceCurrency = observation.price().currency();
        Currency underlyingCurrency = product.terms().underlying().currency();

        if (priceCurrency != underlyingCurrency) {
            throw new IllegalArgumentException(
                    "observation price currency %s does not match underlying currency %s"
                            .formatted(priceCurrency, underlyingCurrency));
        }

        return switch (product) {
            case Autocallable autocallable -> evaluateAutocallable(autocallable, observation);
            case BarrierReverseConvertible brc -> evaluateBrc(brc, observation);
            case FixedCouponNote note -> evaluateFixedCouponNote(note, observation);
        };
    }

    private BigDecimal performance(BigDecimal price, BigDecimal initialPrice) {
        return price.divide(initialPrice, 10, RoundingMode.HALF_EVEN);
    }

    private Money periodCoupon(CommonTerms terms) {
        BigDecimal notional = terms.notional().amount();
        BigDecimal rate = terms.couponRate().fraction();
        int months = terms.frequency().months();
        BigDecimal amount =  notional.multiply(rate).multiply(new BigDecimal(months))
                .divide(new BigDecimal("12"), 10, RoundingMode.HALF_EVEN);

        return new Money(amount, terms.notional().currency());
    }

    private Money maturityRedemption(Money notional, BigDecimal performance,
                                     BigDecimal capitalBarrier) {
        if (performance.compareTo(capitalBarrier) >= 0) {
            return notional;
        } else {
            return notional.times(performance);
        }
    }

    private ObservationOutcome evaluateFixedCouponNote(FixedCouponNote note, Observation observation) {
        Money coupon = periodCoupon(note.terms());
        if (observation.finalObservation()) {
            return new Redeemed(coupon, note.terms().notional(), RedemptionReason.MATURITY);
        }
        return new Continues(coupon);
    }

    private ObservationOutcome evaluateAutocallable(Autocallable autocallable, Observation observation) {
        CommonTerms terms = autocallable.terms();
        BigDecimal performance = performance(observation.price().amount(),
                terms.initialPrice().amount());
        Money coupon = performance.compareTo(autocallable.couponBarrier().fraction()) >= 0 ?
            periodCoupon(terms) : Money.of("0", terms.notional().currency());

        if (observation.finalObservation()) {
            Money redemption = maturityRedemption(terms.notional(), performance,
                    autocallable.capitalBarrier().fraction());
            return new Redeemed(coupon, redemption, RedemptionReason.MATURITY);
        }
        if (performance.compareTo(autocallable.autocallTrigger().fraction()) >= 0) {
            return new Redeemed(coupon, terms.notional(), RedemptionReason.AUTOCALL);
        }
        return new Continues(coupon);
    }

    private ObservationOutcome evaluateBrc(BarrierReverseConvertible brc, Observation observation) {
        Money coupon = periodCoupon(brc.terms());
        if (observation.finalObservation()) {
            BigDecimal performance = performance(observation.price().amount(), brc.terms().initialPrice().amount());
            Money redemption = maturityRedemption(brc.terms().notional(), performance, brc.capitalBarrier().fraction());
            return new Redeemed(coupon, redemption, RedemptionReason.MATURITY);
        }
        return new Continues(coupon);
    }

}
