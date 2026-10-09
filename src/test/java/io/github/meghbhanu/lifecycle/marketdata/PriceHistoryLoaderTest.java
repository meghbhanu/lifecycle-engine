package io.github.meghbhanu.lifecycle.marketdata;

import io.github.meghbhanu.lifecycle.domain.Currency;
import io.github.meghbhanu.lifecycle.domain.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PriceHistoryLoaderTest {

    private final PriceHistoryLoader loader = new PriceHistoryLoader();

    @Test
    void validFileLoads(@TempDir Path dir) throws IOException {
        CharSequence csv = """
                date,price,currency
                2026-10-05,210,USD
                2026-10-02,200,USD
                """;

        Path file = Files.writeString(dir.resolve("prices.csv"), csv);
        LoadResult<PriceHistory> result = loader.load(file);

        assertEquals(2, result.value().size());
        assertFalse(result.hasErrors());
        assertEquals(Optional.of(Money.of("210", Currency.USD)),
                result.value().priceOn(LocalDate.of(2026, 10, 5)));
    }

    @Test
    void priceOnMissingDateReturnsEmpty(@TempDir Path dir) throws IOException {
        CharSequence csv = """
                date,price,currency
                2026-10-05,210,USD
                2026-10-02,200,USD
                """;

        Path file = Files.writeString(dir.resolve("prices.csv"), csv);
        LoadResult<PriceHistory> result = loader.load(file);

        assertEquals(Optional.empty(), result.value().priceOn(
                LocalDate.of(2026, 10, 24)));
    }

    @Test
    void goodAndBadRowsAreParsedCorrectly(@TempDir Path dir) throws IOException {
        CharSequence csv = """
                date,price,currency
                2026-10-05,210,USD
                2026-10-02,200,USD
                2026-10-05,abc,USD
                2026-10-0,200,USD
                2026-10-05,210
                """;

        Path file = Files.writeString(dir.resolve("prices.csv"), csv);
        LoadResult<PriceHistory> result = loader.load(file);

        List<LoadError> errors = result.errors();
        PriceHistory history = result.value();

        assertEquals(3, errors.size());
        assertEquals(4, errors.getFirst().lineNumber());
        assertEquals(5, errors.get(1).lineNumber());
        assertEquals(6, errors.get(2).lineNumber());
        assertEquals(2, history.size());
    }

    @Test
    void duplicateDatesInEntriesAreCaughtInErrors(@TempDir Path dir) throws IOException {
        CharSequence csv = """
                date,price,currency
                2026-10-05,210,USD
                2026-10-05,200,USD
                """;

        Path file = Files.writeString(dir.resolve("prices.csv"), csv);
        LoadResult<PriceHistory> result = loader.load(file);

        assertEquals(1, result.value().size());
        assertEquals(Optional.of(Money.of("210", Currency.USD)),
                result.value().priceOn(LocalDate.of(2026, 10, 5)));   // the FIRST value was kept
        assertEquals(1, result.errors().size());
        assertEquals(3, result.errors().getFirst().lineNumber());
    }

    @Test
    void wrongHeaderThrowsCsvFormatException(@TempDir Path dir) throws IOException {
        CharSequence csv = """
                date,price,current
                2026-10-05,210,USD
                2026-10-05,200,USD
                """;

        Path file = Files.writeString(dir.resolve("prices.csv"), csv);
        CsvFormatException ex = assertThrows(CsvFormatException.class, () -> loader.load(file));
        assertTrue(ex.getMessage().contains("expected header"));
    }

    @Test
    void missingFileThrowsNoSuchFileException(@TempDir Path dir) {
        Path missing = dir.resolve("missing.csv");   // a path only: the file is never written

        assertThrows(NoSuchFileException.class, () -> loader.load(missing));
    }
}
