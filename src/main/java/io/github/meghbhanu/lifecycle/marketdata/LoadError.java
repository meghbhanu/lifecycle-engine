package io.github.meghbhanu.lifecycle.marketdata;

import java.util.Objects;

public record LoadError(int lineNumber, String line, String reason) {
    public LoadError {
        Objects.requireNonNull(line, "line");
        Objects.requireNonNull(reason, "reason");

        if (lineNumber < 1) {
            throw new IllegalArgumentException("line number must be greater than 0");
        }

        if (line.isBlank()) {
            throw new IllegalArgumentException("line information is blank");
        }
        if (reason.isBlank()) {
            throw new IllegalArgumentException("reason is blank");
        }
    }
}
