package com.jotak.mipod.common.vertx;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.streams.ReadStream;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class LineStreamer implements ReadStream<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LineStreamer.class);
    private static final int MAX_ALLOWED_BUFFER_LINES = 8000;

    private final ReadStream<Buffer> source;
    private final Deque<String> bufferLines = new LinkedList<>();
    private Optional<Handler<String>> optHandler = Optional.empty();
    private String previousString = "";
    private boolean paused;

    public LineStreamer(final ReadStream<Buffer> source, final String regexSeparator) {
        this.source = source;
        source.handler(buffer -> {
            String fullString = previousString + buffer.getString(0, buffer.length());
            int lastIdx = fullString.lastIndexOf(regexSeparator);
            if (lastIdx == fullString.length() - 1) {
                previousString = "";
            } else {
                previousString = fullString.substring(lastIdx + 1);
                fullString = fullString.substring(0, lastIdx - 1);
            }
            bufferLines.addAll(Arrays.asList(fullString.split(regexSeparator)));
            processQueue();
            if (bufferLines.size() > MAX_ALLOWED_BUFFER_LINES) {
                // Looks like back pressure is becomming too high
                LOGGER.warn("Discarding data due to abnormal back pressure. Maybe no handler connected?");
                bufferLines.clear();
            }
        });
    }

    @Override
    public ReadStream<String> exceptionHandler(final Handler<Throwable> handler) {
        source.exceptionHandler(handler);
        return this;
    }

    @Override
    public ReadStream<String> handler(final Handler<String> handler) {
        optHandler = Optional.of(handler);
        return this;
    }

    public CompletableFuture<String> readLine() {
        final CompletableFuture<String> fut = new CompletableFuture<>();
        final Optional<Handler<String>> actualHandler = optHandler;
        optHandler = Optional.of((Handler<String>) (value) -> {
            optHandler = actualHandler;
            fut.complete(value);
        });
        return fut;
    }

    public CompletableFuture<LineStreamer> expect(final Pattern pattern) {
        final CompletableFuture<LineStreamer> fut = new CompletableFuture<>();
        final Optional<Handler<String>> actualHandler = optHandler;
        optHandler = Optional.of(line -> {
            optHandler = actualHandler;
            if (pattern.matcher(line).matches()) {
                fut.complete(this);
            } else {
                fut.completeExceptionally(new ExpectationNotFulfilledException("Got \"" + line + "\" instead of /" + pattern + "/"));
            }
        });
        return fut;
    }

    private void processQueue() {
        optHandler.ifPresent(handler -> {
            while (!bufferLines.isEmpty()) {
                final String line = bufferLines.pop();
                handler.handle(line);
                if (paused) {
                    break;
                }
            }
        });
    }

    @Override
    public ReadStream<String> pause() {
        source.pause();
        paused = true;
        return this;
    }

    @Override
    public ReadStream<String> resume() {
        source.resume();
        processQueue();
        return this;
    }

    @Override
    public ReadStream<String> endHandler(final Handler<Void> endHandler) {
        source.endHandler(endHandler);
        return this;
    }
}
