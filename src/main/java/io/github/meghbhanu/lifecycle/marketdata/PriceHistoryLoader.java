package io.github.meghbhanu.lifecycle.marketdata;

import io.github.meghbhanu.lifecycle.domain.Money;
import io.github.meghbhanu.lifecycle.domain.Currency;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public final class PriceHistoryLoader {

    public LoadResult<PriceHistory> load(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        if (lines.isEmpty()) {
            throw new CsvFormatException("file is empty");
        }
        if (!lines.getFirst().equals("date,price,currency")) {
            throw new CsvFormatException(
                    "line 1: expected header 'date,price,currency', found: %s".formatted(
                            lines.getFirst()));
        }

        NavigableMap<LocalDate, Money> goodPrices = new TreeMap<>();
        List<LoadError> errors = new ArrayList<>();
        Currency expectedCurrency = null;

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNumber = i + 1;
            if (line.isBlank()) continue;
            try {
                Row row = parseRow(line);
                if (goodPrices.containsKey(row.date())) {
                    throw new IllegalArgumentException(
                            "duplicate date %s, first value kept".formatted(row.date()));
                }
                if (expectedCurrency != null && expectedCurrency != row.price().currency()) {
                    throw new IllegalArgumentException(
                            "currency %s does not match %s".formatted(row.price().currency(), expectedCurrency));
                }
                goodPrices.put(row.date(), row.price());
                if (expectedCurrency == null) {
                    expectedCurrency = row.price().currency();
                }
            } catch (DateTimeParseException | IllegalArgumentException e) {
                errors.add(new LoadError(lineNumber, line, e.getMessage()));
            }
        }

        PriceHistory prices = new PriceHistory(goodPrices);

        return new LoadResult<>(prices, errors);
    }

    private static Row parseRow(String line) {
        String[] values = line.split(",", -1);
        if (values.length != 3) throw new IllegalArgumentException(
                "expected 3 columns, got " + values.length);
        String date = values[0].strip();
        String price = values[1].strip();
        String currency = values[2].strip();

        LocalDate parsedDate = LocalDate.parse(date);
        Currency parsedCurrency = Currency.valueOf(currency.toUpperCase(Locale.ROOT));
        BigDecimal parsedPrice = new BigDecimal(price);

        if (parsedPrice.signum() <= 0) {
            throw new IllegalArgumentException("price must be > 0");
        }

        return new Row(parsedDate, Money.of(parsedPrice, parsedCurrency));
    }

    private record Row(LocalDate date, Money price) {}
}
