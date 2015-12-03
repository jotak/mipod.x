package com.jotak.mipod.mpd;

import com.jotak.mipod.configuration.MpdClientConfiguration;
import com.jotak.mipod.data.audio.Song;
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
        final MpdClient mpdClient = new MpdClient(vertx, new MpdClientConfiguration("localhost", 6600));
        final MpdPublisher mpdPublisher = new MpdPublisher(vertx.eventBus());
        mpdPublisher.connect(mpdClient);

        initializeConsumers(vertx.eventBus(), mpdClient);
    }

    private static void initializeConsumers(final EventBus eventBus, final MpdClient mpdClient) {
        eventBus.consumer(GET_CURRENT_TRACK).handler(
                event -> mpdClient.getCurrent()
                        .thenAccept(optTrack -> event.reply(optTrack.map(Song::toJson).orElse(null)))
                        .exceptionally(logException("Could not get current track")));

        eventBus.consumer(PLAY).handler(
                event -> mpdClient.play()
                        .exceptionally(logException("Could not play")));

        eventBus.consumer(STOP).handler(
                event -> mpdClient.stop()
                        .exceptionally(logException("Could not stop")));

        eventBus.consumer(PREV).handler(
                event -> mpdClient.previous()
                        .exceptionally(logException("Could not go to previous")));

        eventBus.consumer(NEXT).handler(
                event -> mpdClient.next()
                        .exceptionally(logException("Could not go to next")));
    }

    private static Function<Throwable, Void> logException(final String context) {
        return t -> {
            LOGGER.error(context, t);
            return null;
        };
    }
}
