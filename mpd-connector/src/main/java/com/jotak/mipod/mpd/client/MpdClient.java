package com.jotak.mipod.mpd.client;

import com.google.common.base.Joiner;
import com.jotak.mipod.common.vertx.LineStreamer;
import com.jotak.mipod.configuration.MpdClientConfiguration;
import com.jotak.mipod.data.audio.Song;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import static com.jotak.mipod.mpd.client.MpdCommands.*;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class MpdClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MpdClient.class);
    private static final int CONNECTION_TIMEOUT = 100000;
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int RECONNECT_ATTEMPTS = 3;
    private static final int RECONNECT_INTERVAL_MS = 2000;
    private static final String MPD_RESPONSE_OK = "OK";
    private static final Pattern MPD_CONNECT_OK = Pattern.compile("^OK MPD.*$");

    private final Vertx vertx;
    private final MpdClientConfiguration configuration;
    private final CompletableFuture<ConnectionInstance> connectionIdle;

    public MpdClient(final Vertx vertx, final MpdClientConfiguration configuration) {
        this.vertx = vertx;
        this.configuration = configuration;
        connectionIdle = connect();
    }

    private CompletableFuture<ConnectionInstance> connect() {
        LOGGER.info("Connecting to MPD...");
        final CompletableFuture<NetSocket> netSocketReady = new CompletableFuture<>();
        vertx.createNetClient(new NetClientOptions()
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setReceiveBufferSize(DEFAULT_BUFFER_SIZE)
                .setReconnectAttempts(RECONNECT_ATTEMPTS)
                .setReconnectInterval(RECONNECT_INTERVAL_MS)
                .setTcpKeepAlive(true))
                .connect(configuration.getPort(), configuration.getHostname(), res -> {
                    if (res.succeeded()) {
                        netSocketReady.complete(res.result());
                        LOGGER.info("Connection successful");
                    } else {
                        error("Connection failure", res.cause());
                        netSocketReady.completeExceptionally(res.cause());
                    }
                });
        return netSocketReady
                .thenApply(netSocket -> {
                    LineStreamer ls = new LineStreamer(netSocket, "\n");
                    LOGGER.info(ls);
                    return ls;
                })
                .thenCompose(lineStreamer -> lineStreamer.expect(MPD_CONNECT_OK))
                .thenCombine(netSocketReady, ConnectionInstance::new)
                .exceptionally(t -> {
                    error("Initial MPD connection failure: ", t);
                    return null;
                });
    }

    public void idleLoop(final String options, final Consumer<String> consumer) {
        connectionIdle
                .thenApply(conn -> {
                    conn.getNetSocket().write(IDLE + " " + options + "\n");
                    return conn;
                })
                .thenCompose(conn -> conn.getLineStreamer().readLine())
                .thenAccept(consumer::accept)
                .thenCombine(connectionIdle, (a, conn) -> conn.getLineStreamer().expect(MPD_CONNECT_OK))
                .thenRun(() -> idleLoop(options, consumer))
                .exceptionally(t -> {
                    error("Error in idleLoop", t);
                    vertx.setTimer(5000, l -> idleLoop(options, consumer));
                    return null;
                });
    }

    public CompletableFuture<Optional<Song>> getCurrent() {
        final CompletableFuture<Optional<Song>> fut = new CompletableFuture<>();
        connect()
                .thenApply(command(CURRENT_SONG))
                .thenAccept(conn -> new MpdParser(conn.getLineStreamer(), MPD_RESPONSE_OK)
                        .handler(item -> {
                            if (item instanceof Song) {
                                fut.complete(Optional.of((Song) item));
                            } else {
                                LOGGER.error("Unexpected response from MPD, not a Song: " + item);
                                fut.complete(Optional.<Song>empty());
                            }
                            conn.netSocket.close();
                        }).endHandler(aVoid -> {
                            if (!fut.isDone()) {
                                fut.complete(Optional.<Song>empty());
                                conn.netSocket.close();
                            }
                        }))
                .exceptionally(ex -> {
                    fut.completeExceptionally(ex);
                    return null;
                });
        return fut;
    }

    public CompletableFuture<Void> play() {
        return commandAndClose(PLAY);
    }

    public CompletableFuture<Void> stop() {
        return commandAndClose(STOP);
    }

    public CompletableFuture<Void> pause() {
        return commandAndClose(PAUSE);
    }

    public CompletableFuture<Void> previous() {
        return commandAndClose(PREV);
    }

    public CompletableFuture<Void> next() {
        return commandAndClose(NEXT);
    }

    public CompletableFuture<Void> clear() {
        return commandAndClose(PL_CLEAR);
    }

    public CompletableFuture<Void> add(final String path) {
        return connect()
                .thenApply(addNoClose(path))
                .thenAccept(close());
    }

    private FnConnectionInstance addNoClose(final String path) {
        final String cmd;
        // Playlists need to be "loaded" instead of "added"
        if (path.endsWith(".m3U")
                || path.endsWith(".pls")
                || !path.contains("/") /* For MPD playlists */) {
            cmd = PL_LOAD;
        } else {
            cmd = PL_ADD;
        }
        return command(cmd, "\"" + path + "\"");
    }

    public CompletableFuture<Void> playEntry(final String path) {
        return connect()
                .thenApply(command(PL_CLEAR))
                .thenApply(addNoClose(path))
                .thenApply(command(PLAY))
                .thenAccept(close());
    }

    public CompletableFuture<Void> playIdx(final int idx) {
        return commandAndClose(PLAY, String.valueOf(idx));
    }

    public CompletableFuture<Void> volume(final int vol) {
        return commandAndClose(VOLUME, String.valueOf(vol));
    }

    public CompletableFuture<Void> repeat(final boolean enable) {
        return commandAndClose(REPEAT, enable ? "1" : "0");
    }

    public CompletableFuture<Void> random(final boolean enable) {
        return commandAndClose(RANDOM, enable ? "1" : "0");
    }

    public CompletableFuture<Void> single(final boolean enable) {
        return commandAndClose(SINGLE, enable ? "1" : "0");
    }

    public CompletableFuture<Void> consume(final boolean enable) {
        return commandAndClose(CONSUME, enable ? "1" : "0");
    }

    private CompletableFuture<Void> commandAndClose(final String... args) {
        return connect()
                .thenApply(command(args))
                .thenAccept(close());
    }

    private FnConnectionInstance command(final String... args) {
        return inst -> {
            inst.getNetSocket().write(Joiner.on(' ').join(args) + "\n");
            return inst;
        };
    }

    private static Consumer<ConnectionInstance> close() {
        return conn -> conn.netSocket.close();
    }

    private static void error(final String message, final Throwable t) {
        LOGGER.error(message);
        t.printStackTrace();
    }

    private static final class ConnectionInstance {
        private final NetSocket netSocket;
        private final LineStreamer lineStreamer;

        private ConnectionInstance(final LineStreamer lineStreamer, final NetSocket netSocket) {
            this.netSocket = netSocket;
            this.lineStreamer = lineStreamer;
        }

        NetSocket getNetSocket() {
            return netSocket;
        }

        LineStreamer getLineStreamer() {
            return lineStreamer;
        }
    }

    private interface FnConnectionInstance extends Function<ConnectionInstance, ConnectionInstance> {}
}
