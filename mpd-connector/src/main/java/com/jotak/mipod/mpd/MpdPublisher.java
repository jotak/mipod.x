package com.jotak.mipod.mpd;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.jotak.mipod.data.audio.Song;
import com.jotak.mipod.mpd.client.MpdClient;
import io.vertx.core.eventbus.EventBus;

import java.util.Optional;
import java.util.Set;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
class MpdPublisher {

    /**
     * database: the song database has been modified after update.
     */
    private static final String SUBSYSTEM_DATABASE = "database";
    /**
     * update: a database update has started or finished. If the database was modified during the update, the database event is also emitted.
     */
    private static final String SUBSYSTEM_UPDATE = "update";
    /**
     * stored_playlist: a stored playlist has been modified, renamed, created or deleted
     */
    private static final String SUBSYSTEM_STORED_PL = "stored_playlist";
    /**
     * playlist: the current playlist has been modified
     */
    private static final String SUBSYSTEM_PLAYLIST = "playlist";
    /**
     * player: the player has been started, stopped or seeked
     */
    private static final String SUBSYSTEM_PLAYER = "player";
    /**
     * mixer: the volume has been changed
     */
    private static final String SUBSYSTEM_MIXER = "mixer";
    /**
     * output: an audio output has been enabled or disabled
     */
    private static final String SUBSYSTEM_OUTPUT = "output";
    /**
     * options: options like repeat, random, crossfade, replay gain
     */
    private static final String SUBSYSTEM_OPTIONS = "options";
    /**
     * sticker: the sticker database has been modified.
     */
    private static final String SUBSYSTEM_STICKER = "sticker";
    /**
     * subscription: a client has subscribed or unsubscribed to a channel
     */
    private static final String SUBSYSTEM_SUBSCRIPTION = "subscription";
    /**
     * message: a message was received on a channel this client is subscribed to; this event is only emitted when the queue is empty
     */
    private static final String SUBSYSTEM_MESSAGE = "message";

    private static final Set<String> IDLE_OPTIONS = ImmutableSet.of(/*SUBSYSTEM_PLAYER*/);
    private final EventBus eventBus;
    private Optional<Song> currentSong = Optional.empty();

    MpdPublisher(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    void connect(final MpdClient mpdClient) {
        mpdClient.idleLoop(Joiner.on(' ').join(IDLE_OPTIONS),
                line -> {
                    connect(mpdClient);
                    onIdleOut(mpdClient, line);
                });
    }

    private void onIdleOut(final MpdClient mpdClient, final String subsystem) {
        if (subsystem.endsWith(SUBSYSTEM_PLAYER)) {
            mpdClient.getCurrent().thenAccept(optSong -> {
                if (!optSong.equals(currentSong)) {
                    currentSong = optSong;
                    eventBus.publish(MpdEventBusAddresses.TRACK_CHANGED, optSong.map(Song::toJson).orElse(null));
                }
            });
        }
    }
}
