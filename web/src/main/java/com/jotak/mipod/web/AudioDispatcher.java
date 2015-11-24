package com.jotak.mipod.web;

import com.jotak.mipod.mpd.MpdEventBusAddresses;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
final class AudioDispatcher {

    private AudioDispatcher() {
    }

    static void registerEvents(final EventBus eventBus) {
        eventBus.consumer(WebEventBusAddresses.AUDIO_PLAY).handler(event -> onPlay(eventBus));
        eventBus.consumer(WebEventBusAddresses.AUDIO_STOP).handler(event -> onStop(eventBus));
        eventBus.consumer(WebEventBusAddresses.AUDIO_NEXT).handler(event -> onNext(eventBus));
        eventBus.consumer(WebEventBusAddresses.AUDIO_PREV).handler(event -> onPrev(eventBus));
    }

    private static void onPlay(final EventBus eventBus) {
        eventBus.publish(MpdEventBusAddresses.PLAY, new JsonObject());
    }

    private static void onStop(final EventBus eventBus) {
        eventBus.publish(MpdEventBusAddresses.STOP, new JsonObject());
    }

    private static void onPrev(final EventBus eventBus) {
        eventBus.publish(MpdEventBusAddresses.PREV, new JsonObject());
    }

    private static void onNext(final EventBus eventBus) {
        eventBus.publish(MpdEventBusAddresses.NEXT, new JsonObject());
    }
}
