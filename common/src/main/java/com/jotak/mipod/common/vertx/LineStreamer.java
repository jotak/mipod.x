package com.jotak.mipod.common.vertx;

import com.jotak.mipod.common.lambda.Else;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.streams.ReadStream;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class LineStreamer implements ReadStream<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LineStreamer.class);
    private static final int MAX_ALLOWED_BUFFER_LINES = 8000;

    private final ReadStream<Buffer> source;
    private final Queue<String> bufferLines = new LinkedList<>();
    private final Queue<Handler<String>> handlers = new LinkedList<>();
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
        handlers.offer(handler);
        if (handlers.size() == 1) {
            // Was empty
            resume();
        }
        return this;
    }

    public ReadStream<String> pollHandler() {
        handlers.poll();
        return this;
    }

    public CompletableFuture<String> readLine() {
        final CompletableFuture<String> fut = new CompletableFuture<>();
        handler(value -> {
            pollHandler();
            fut.complete(value);
        });
        return fut;
    }

    public CompletableFuture<LineStreamer> expect(final Pattern pattern) {
        final CompletableFuture<LineStreamer> fut = new CompletableFuture<>();
        handler(line -> {
            pollHandler();
            if (pattern.matcher(line).matches()) {
                fut.complete(this);
            } else {
                fut.completeExceptionally(new ExpectationNotFulfilledException("Got \"" + line + "\" instead of /" + pattern + "/"));
            }
        });
        return fut;
    }

    private Else ifHandler(final Consumer<Handler<String>> consumer) {
        if (!handlers.isEmpty()) {
            consumer.accept(handlers.peek());
            return (r) -> {};
        } else {
            return Runnable::run;
        }
    }

    private void processQueue() {
        if (paused || bufferLines.isEmpty()) {
            return;
        }
        ifHandler(handler -> {
            handler.handle(bufferLines.poll());
            processQueue();
        })._else(this::pause);
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
        paused = false;
        processQueue();
        return this;
    }

    @Override
    public ReadStream<String> endHandler(final Handler<Void> endHandler) {
        source.endHandler(endHandler);
        return this;
    }
}
