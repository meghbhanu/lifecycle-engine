package io.github.meghbhanu.lifecycle.marketdata;

public class CsvFormatException extends RuntimeException {
    public CsvFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvFormatException(String message) {
        super(message);
    }
}
