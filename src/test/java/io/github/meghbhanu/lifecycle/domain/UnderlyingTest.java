package io.github.meghbhanu.lifecycle.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnderlyingTest {

    @Test
    void tickerIsStoredInCapitalLetters() {
        assertEquals("AAPL", new Underlying("aapl", Currency.USD).ticker());
    }

    @Test
    void blankTickerThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Underlying("  ", Currency.USD));
    }

    @Test
    void surroundingSpacesAreRemovedInTicker() {
        assertEquals("AAPL", new Underlying(" AAPL ", Currency.USD).ticker());
    }
}
