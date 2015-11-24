package com.jotak.mipod.mpd.client;

import com.jotak.mipod.data.audio.Item;
import com.jotak.mipod.data.audio.Song;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.jotak.mipod.mpd.client.MpdCommands.*;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class MpdClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MpdClient.class);
    private static final int CONNECTION_TIMEOUT = 100000;
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final String MPD_RESPONSE_OK = "OK";

    private final InetSocketAddress socketAddress;

    MpdClient(final String hostname, final int port) {
        socketAddress = new InetSocketAddress(hostname, port);
    }

    public Optional<Song> getCurrent() {
        return connectAndCommand(CURRENT_SONG, in -> {
            final List<Item> lst = MpdParser.parseEntries(readToOK(in));
            if (lst.isEmpty()) {
                return Optional.<Song>empty();
            }
            final Item item = lst.get(0);
            if (item instanceof Song) {
                return Optional.of((Song) lst.get(0));
            }
            LOGGER.error("Unexpected response from MPD, not a Song: " + item);
            return Optional.<Song>empty();
        }).flatMap(Function.identity());
    }

    public void play() {
        connectAndCommand(PLAY);
    }

    public void stop() {
        connectAndCommand(STOP);
    }

    public void previous() {
        connectAndCommand(PREV);
    }

    public void next() {
        connectAndCommand(NEXT);
    }

    private void connectAndCommand(final String command) {
        try (Socket socket = new Socket()) {
            final Pair<BufferedReader, OutputStreamWriter> inOut = connect(socket);
            command(inOut.getRight(), command);
        } catch (final IOException e) {
            LOGGER.error("MPD connection error", e);
        }
    }

    private <T> Optional<T> connectAndCommand(final String command, final Function<BufferedReader, T> processor) {
        try (Socket socket = new Socket()) {
            final Pair<BufferedReader, OutputStreamWriter> inOut = connect(socket);
            command(inOut.getRight(), command);
            return Optional.ofNullable(processor.apply(inOut.getLeft()));
        } catch (final IOException e) {
            LOGGER.error("MPD connection error", e);
            return Optional.empty();
        }
    }

    private Pair<BufferedReader, OutputStreamWriter> connect(final Socket socket) throws IOException {
        socket.connect(socketAddress, CONNECTION_TIMEOUT);
        final InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream(), "UTF-8");
        final BufferedReader in = new BufferedReader(inputStreamReader, DEFAULT_BUFFER_SIZE);
        final OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
        final String line = in.readLine();
        if (line == null) {
            throw new IOException("No response from server");
        }
        if (!line.startsWith(MPD_RESPONSE_OK)) {
            throw new IOException("Unexpected response from MPD server");
        }
        return Pair.of(in, out);
    }

    private static void command(final OutputStreamWriter out, final String command) throws IOException {
        out.write(command + "\n");
        out.flush();
    }

    private static List<String> readToOK(final BufferedReader in) {
        final List<String> lines = new ArrayList<>();
        String line;
        try {
            while ((line = in.readLine()) != null) {
                if (MPD_RESPONSE_OK.equals(line)) {
                    break;
                }
                lines.add(line);
            }
        } catch (final IOException e) {
            LOGGER.error("Could not read from server", e);
        }
        return lines;
    }
}
