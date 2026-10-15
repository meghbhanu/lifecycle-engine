package io.github.meghbhanu.lifecycle.engine;

import io.github.meghbhanu.lifecycle.domain.Money;
import io.github.meghbhanu.lifecycle.domain.Percentage;
import io.github.meghbhanu.lifecycle.domain.TestTerms;
import io.github.meghbhanu.lifecycle.marketdata.PriceSource;
import io.github.meghbhanu.lifecycle.payoff.PayoffCalculator;
import io.github.meghbhanu.lifecycle.product.Autocallable;
import io.github.meghbhanu.lifecycle.schedule.ScheduledDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.meghbhanu.lifecycle.domain.Currency.USD;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LifecycleEngineMockTest {

    private static final String ID = "AC-001";

    private static final LocalDate Q1 = LocalDate.of(2027, 1, 4);
    private static final LocalDate Q2 = LocalDate.of(2027, 4, 2);
    private static final LocalDate Q3 = LocalDate.of(2027, 7, 2);

    private static final boolean NO_MEMORY = false;

    @Mock
    private PriceSource prices;

    private final LifecycleEngine engine = new LifecycleEngine(new PayoffCalculator());

    @Test
    void engineStopsAskingForPricesAfterAutocall() {
        
        when(prices.priceOn(Q1)).thenReturn(Optional.of(Money.of("205", USD)));

        engine.run(autocallable(NO_MEMORY), schedule(Q1, Q2, Q3), prices);

        verify(prices).priceOn(Q1);
        verify(prices, never()).priceOn(Q2);
        verify(prices, never()).priceOn(Q3);
    }

    private static Autocallable autocallable(boolean memory) {
        return new Autocallable(
                TestTerms.common(ID),
                Percentage.ofPercent("70"),
                Percentage.ofPercent("100"),
                Percentage.ofPercent("60"),
                memory);
    }

    private static List<ScheduledDate> schedule(LocalDate... dates) {
        List<ScheduledDate> result = new ArrayList<>();
        for (LocalDate date : dates) {
            result.add(new ScheduledDate(date, date));
        }
        return result;
    }
}