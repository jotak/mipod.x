package com.jotak.mipod.mpd;

import com.jotak.mipod.data.audio.Song;
import com.jotak.mipod.mpd.client.AsyncMpdClient;
import com.jotak.mipod.mpd.client.MpdClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.function.Function;

import static com.jotak.mipod.mpd.MpdEventBusAddresses.*;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class MpdVerticle extends AbstractVerticle {

    public static final String NAME = MpdVerticle.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(MpdVerticle.class);

    @Override
    public void start() throws Exception {
        LOGGER.info("Starting MpdVerticle");
        final AsyncMpdClient asyncMpdClient = new AsyncMpdClient(vertx, "localhost", 6600);

        initializeConsumers(vertx.eventBus(), asyncMpdClient);
    }

    private static void initializeConsumers(final EventBus eventBus, final AsyncMpdClient asyncMpdClient) {
        eventBus.consumer(GET_CURRENT_TRACK).handler(
                event -> asyncMpdClient.get(MpdClient::getCurrent)
                        .thenAccept(optTrack -> event.reply(optTrack.map(Song::toJson).orElse(null)))
                        .exceptionally(logException("Could not get current track")));

        eventBus.consumer(PLAY).handler(
                event -> asyncMpdClient.get(MpdClient::play)
                        .exceptionally(logException("Could not play")));

        eventBus.consumer(STOP).handler(
                event -> asyncMpdClient.get(MpdClient::stop)
                        .exceptionally(logException("Could not stop")));

        eventBus.consumer(PREV).handler(
                event -> asyncMpdClient.get(MpdClient::previous)
                        .exceptionally(logException("Could not go to previous")));

        eventBus.consumer(NEXT).handler(
                event -> asyncMpdClient.get(MpdClient::next)
                        .exceptionally(logException("Could not go to next")));
    }

    private static Function<Throwable, Void> logException(final String context) {
        return t -> {
            LOGGER.error(context, t);
            return null;
        };
    }
}
