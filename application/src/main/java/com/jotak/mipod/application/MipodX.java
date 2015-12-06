package com.jotak.mipod.application;

import com.jotak.mipod.mpd.MpdVerticle;
import com.jotak.mipod.web.WebVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class MipodX extends AbstractVerticle {

    static {
        Logging.initialize();
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(MipodX.class);

    @Override
    public void start() throws Exception {
        LOGGER.info("Starting MipodX");
        deployVerticle(MpdVerticle.NAME)
                .thenCompose(s -> deployVerticle(WebVerticle.NAME))
                .exceptionally(throwable -> {
                    LOGGER.error("Deployment error", throwable);
                    return null;
                });
    }

    private CompletableFuture<String> deployVerticle(final String name) {
        final CompletableFuture<String> fut = new CompletableFuture<>();
        vertx.deployVerticle(name, res -> {
            if (res.succeeded()) {
                fut.complete(res.result());
            } else {
                fut.completeExceptionally(res.cause());
            }
        });
        return fut;
    }
}
