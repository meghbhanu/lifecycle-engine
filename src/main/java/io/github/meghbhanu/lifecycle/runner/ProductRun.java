package io.github.meghbhanu.lifecycle.runner;

import io.github.meghbhanu.lifecycle.marketdata.PriceHistory;
import io.github.meghbhanu.lifecycle.product.Product;
import io.github.meghbhanu.lifecycle.schedule.ScheduledDate;

import java.util.List;
import java.util.Objects;

public record ProductRun(Product product, List<ScheduledDate> schedule, PriceHistory prices) {
    public ProductRun {
        Objects.requireNonNull(product, "product");
        Objects.requireNonNull(schedule, "schedule");
        Objects.requireNonNull(prices, "prices");
    }
}
