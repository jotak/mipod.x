package com.jotak.mipod.mpd.client;

import com.jotak.mipod.common.vertx.LineStreamer;
import com.jotak.mipod.configuration.MpdClientConfiguration;
import com.jotak.mipod.data.audio.Song;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
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

    private final CompletableFuture<ConnectionInstance> connectionCommand;
    private final CompletableFuture<ConnectionInstance> connectionIdle;

    public MpdClient(final Vertx vertx, final MpdClientConfiguration configuration) {
        final NetClient netClient = vertx.createNetClient(new NetClientOptions()
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setReceiveBufferSize(DEFAULT_BUFFER_SIZE)
                .setReconnectAttempts(RECONNECT_ATTEMPTS)
                .setReconnectInterval(RECONNECT_INTERVAL_MS)
                .setTcpKeepAlive(true));

        connectionCommand = connect(netClient, configuration);
        connectionIdle = connect(netClient, configuration);
    }

    private static CompletableFuture<ConnectionInstance> connect(final NetClient netClient, final MpdClientConfiguration configuration) {
        LOGGER.info("Connecting to MPD...");
        final CompletableFuture<NetSocket> netSocketReady = new CompletableFuture<>();
        netClient.connect(configuration.getPort(), configuration.getHostname(), res -> {
            if (res.succeeded()) {
                netSocketReady.complete(res.result());
                LOGGER.info("Connection successful");
            } else {
                LOGGER.error("Connection failure", res.cause());
                netSocketReady.completeExceptionally(res.cause());
            }
        });
        return netSocketReady
                .thenApply(netSocket -> new LineStreamer(netSocket, "\n"))
                .thenCompose(lineStreamer -> lineStreamer.expect(MPD_CONNECT_OK))
                .thenCombine(netSocketReady, ConnectionInstance::new)
                .exceptionally(t -> {
                    LOGGER.error("Initial MPD connection failure: ", t);
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
                .thenAccept(s -> {
                    consumer.accept(s);
                    idleLoop(options, consumer);
                }).exceptionally(t -> {
                    LOGGER.error(t);
                    idleLoop(options, consumer);
                    return null;
                });
    }

    public CompletableFuture<Optional<Song>> getCurrent() {
        final CompletableFuture<Optional<Song>> fut = new CompletableFuture<>();
        command(CURRENT_SONG)
                .thenAccept(connectionInstance -> new MpdParser(
                        connectionInstance.getLineStreamer(),
                        item -> {
                            if (item instanceof Song) {
                                fut.complete(Optional.of((Song) item));
                            } else {
                                LOGGER.error("Unexpected response from MPD, not a Song: " + item);
                                fut.complete(Optional.<Song>empty());
                            }
                        },
                        aVoid -> {
                            if (!fut.isDone()) {
                                fut.complete(Optional.<Song>empty());
                            }
                        },
                        MPD_RESPONSE_OK))
                .exceptionally(ex -> {
                    fut.completeExceptionally(ex);
                    return null;
                });
        return fut;
    }

    public CompletableFuture<Void> play() {
        return command(PLAY).thenAccept(a -> {});
    }

    public CompletableFuture<Void> stop() {
        return command(STOP).thenAccept(a -> {});
    }

    public CompletableFuture<Void> previous() {
        return command(PREV).thenAccept(a -> {});
    }

    public CompletableFuture<Void> next() {
        return command(NEXT).thenAccept(a -> {});
    }

    private CompletableFuture<ConnectionInstance> command(final String command) {
        return connectionCommand.thenApply(inst -> {
            inst.getNetSocket().write(command + "\n");
            return inst;
        });
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
}
