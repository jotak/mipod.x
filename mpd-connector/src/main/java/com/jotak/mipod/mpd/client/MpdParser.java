package com.jotak.mipod.mpd.client;

import com.google.common.collect.Maps;
import com.jotak.mipod.common.vertx.LineStreamer;
import com.jotak.mipod.data.audio.Directory;
import com.jotak.mipod.data.audio.Item;
import com.jotak.mipod.data.audio.Playlist;
import com.jotak.mipod.data.audio.Song;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public final class MpdParser {

    private static final Map<String, Song.Fields> FIELDS_BY_MPD_NAME = Maps.uniqueIndex(Song.Fields.asList(), Song.Fields::getMpdName);

    private final Consumer<Item> songsConsumer;
    private final Consumer<Void> endNotifier;
    private final String endToken;
    private JsonObject json;

    public MpdParser(final LineStreamer lineStreamer, final Consumer<Item> songsConsumer, final Consumer<Void> endNotifier, final String endToken) {
        this.songsConsumer = songsConsumer;
        this.endNotifier = endNotifier;
        this.endToken = endToken;
        lineStreamer.handler(this::parse);
    }

    private void parse(final String line) {
        if (line.equals(endToken)) {
            commitCurrent();
            endNotifier.accept(null);
            return;
        }
        final String[] entry = line.split(": ", 2);
        final String key = entry[0];
        final String value = entry.length > 1 ? entry[1] : "";
        if ("file".equals(key)) {
            commitCurrent();
            json = new JsonObject().put(Song.Fields.FILEPATH.getName(), value);
        } else if ("playlist".equals(key)) {
            commitCurrent();
            json = null;
            songsConsumer.accept(new Playlist(value));
        } else if ("directory".equals(key)) {
            commitCurrent();
            json = null;
            songsConsumer.accept(new Directory(value));
        } else if (json != null) {
            final Song.Fields field = FIELDS_BY_MPD_NAME.get(key);
            if (field != null) {
                json.put(field.getName(), field.parse(value));
            }
        }
    }

    private void commitCurrent() {
        if (json != null) {
            songsConsumer.accept(Song.fromJson(json));
        }
    }
}
