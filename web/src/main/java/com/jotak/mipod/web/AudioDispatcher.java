package com.jotak.mipod.web;

import com.jotak.mipod.mpd.MpdEventBusAddresses;
import io.vertx.core.eventbus.EventBus;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
final class AudioDispatcher {

    private AudioDispatcher() {
    }

    static void registerEvents(final EventBus eventBus) {
        forward(eventBus, WebEventBusAddresses.AUDIO_PLAY, MpdEventBusAddresses.PLAY);
        forward(eventBus, WebEventBusAddresses.AUDIO_STOP, MpdEventBusAddresses.STOP);
        forward(eventBus, WebEventBusAddresses.AUDIO_PAUSE, MpdEventBusAddresses.PAUSE);
        forward(eventBus, WebEventBusAddresses.AUDIO_NEXT, MpdEventBusAddresses.NEXT);
        forward(eventBus, WebEventBusAddresses.AUDIO_PREV, MpdEventBusAddresses.PREV);
    }

    private static void forward(final EventBus eventBus, final String addressIn, final String addressOut) {
        eventBus.consumer(addressIn).handler(event -> eventBus.publish(addressOut, event.body()));
    }
}
