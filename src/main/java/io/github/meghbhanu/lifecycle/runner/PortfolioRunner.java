package io.github.meghbhanu.lifecycle.runner;

import io.github.meghbhanu.lifecycle.engine.LifecycleEngine;
import io.github.meghbhanu.lifecycle.engine.LifecycleEvent;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class PortfolioRunner {

    private final LifecycleEngine engine;
    private final Executor executor;

    public PortfolioRunner(LifecycleEngine engine, Executor executor) {
        this.engine = Objects.requireNonNull(engine, "engine");
        this.executor = Objects.requireNonNull(executor, "executor");
    }

    public List<LifecycleEvent> runSerial(List<ProductRun> runs) {
        Objects.requireNonNull(runs, "runs");
        return runs.stream()
                .map(run -> engine.run(run.product(), run.schedule(), run.prices()))
                .flatMap(List::stream)
                .toList();
    }

    public List<LifecycleEvent> runParallel(List<ProductRun> runs) {
        Objects.requireNonNull(runs, "runs");

        List<CompletableFuture<List<LifecycleEvent>>> futures = runs.stream()
                .map(run -> CompletableFuture.supplyAsync(
                        () -> engine.run(run.product(), run.schedule(), run.prices()),
                        executor))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();
    }
}
