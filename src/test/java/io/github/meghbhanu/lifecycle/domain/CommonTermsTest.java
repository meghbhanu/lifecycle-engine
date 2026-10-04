package io.github.meghbhanu.lifecycle.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CommonTermsTest {

    private Underlying underlying;
    private Money notional;
    private Money initialPrice;
    private LocalDate issueDate;
    private LocalDate maturityDate;

    @BeforeEach
    void setup() {
        underlying   = new Underlying("AAPL", Currency.USD);
        notional     = Money.of("1000000", Currency.USD);
        initialPrice = Money.of("200", Currency.USD);
        issueDate    = LocalDate.of(2026, 10, 2);
        maturityDate = issueDate.plusYears(3);
    }

    @Test
    void validTermsBuild() {
        assertDoesNotThrow(this::terms);
    }

    @Test
    void maturityOnIssueDateThrows() {
        maturityDate = issueDate;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, this::terms);
        assertTrue(ex.getMessage().contains("maturity"));
    }

    @Test
    void zeroNotionalThrows() {
        notional = Money.of("0", Currency.USD);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, this::terms);
        assertTrue(ex.getMessage().contains("notional"));
    }

    @Test
    void initialPriceCurrencyMismatchThrows() {
        initialPrice = Money.of("200", Currency.EUR);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, this::terms);
        assertTrue(ex.getMessage().contains("initial price"));
    }

    private CommonTerms terms() {
        return new CommonTerms("AC-001", underlying, notional, initialPrice,
                Percentage.ofPercent("8"), issueDate, maturityDate, ObservationFrequency.QUARTERLY);
    }
}
