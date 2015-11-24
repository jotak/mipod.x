package com.jotak.mipod.web;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import static com.jotak.mipod.mpd.MpdEventBusAddresses.GET_CURRENT_TRACK;
import static com.jotak.mipod.mpd.MpdEventBusAddresses.TRACK_CHANGED;
import static com.jotak.mipod.web.WebEventBusAddresses.AUDIO_CURRENT;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
final class AudioConsumers {

    private AudioConsumers() {
    }

    static void listenCurrentTrack(final EventBus eventBus) {
        eventBus.consumer(TRACK_CHANGED).handler(event -> eventBus.publish(AUDIO_CURRENT, event.body()));
        eventBus.send(GET_CURRENT_TRACK, null, response -> {
            if (response.succeeded()) {
                eventBus.publish(AUDIO_CURRENT, response.result().body());
            } else {
                eventBus.publish(AUDIO_CURRENT, new JsonObject());
            }
        });
    }
}
