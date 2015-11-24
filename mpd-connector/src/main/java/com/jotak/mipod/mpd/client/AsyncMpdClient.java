package com.jotak.mipod.mpd.client;

import io.vertx.core.Vertx;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class AsyncMpdClient {

    private final Vertx vertx;
    private final MpdClient mpdClient;

    public AsyncMpdClient(final Vertx vertx, final String hostname, final int port) {
        this.vertx = vertx;
        mpdClient = new MpdClient(hostname, port);
    }

    public <T> CompletableFuture<T> get(final Function<MpdClient, T> clbk) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        vertx.<T>executeBlocking(innerFut -> innerFut.complete(clbk.apply(mpdClient)),
            res -> {
                if (res.succeeded()) {
                    future.complete(res.result());
                } else {
                    future.completeExceptionally(res.cause());
                }
            });
        return future;
    }

    public CompletableFuture<Void> get(final Consumer<MpdClient> clbk) {
        return get(mpdClient -> {
            clbk.accept(mpdClient);
            return null;
        });
    }
}
