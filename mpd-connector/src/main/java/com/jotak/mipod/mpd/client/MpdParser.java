package com.jotak.mipod.mpd.client;

import com.google.common.collect.Maps;
import com.jotak.mipod.common.vertx.LineStreamer;
import com.jotak.mipod.data.audio.Directory;
import com.jotak.mipod.data.audio.Item;
import com.jotak.mipod.data.audio.Playlist;
import com.jotak.mipod.data.audio.Song;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;

import java.util.Map;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public final class MpdParser implements ReadStream<Item> {

    private static final Map<String, Song.Fields> FIELDS_BY_MPD_NAME = Maps.uniqueIndex(Song.Fields.asList(), Song.Fields::getMpdName);

    private final LineStreamer lineStreamer;
    private final String endToken;
    private JsonObject json;
    private Handler<Item> itemHandler = a -> {};
    private Handler<Throwable> exceptionHandler = a -> {};
    private Handler<Void> endHandler = a -> {};
    private boolean paused;

    public MpdParser(final LineStreamer lineStreamer, final String endToken) {
        this.endToken = endToken;
        this.lineStreamer = lineStreamer;
        lineStreamer.pause();
        lineStreamer.handler(this::parse);
    }

    @Override
    public ReadStream<Item> exceptionHandler(final Handler<Throwable> handler) {
        this.exceptionHandler = handler;
        return this;
    }

    @Override
    public ReadStream<Item> handler(final Handler<Item> handler) {
        this.itemHandler = handler;
        if (!paused) {
            lineStreamer.resume();
        }
        return this;
    }

    @Override
    public ReadStream<Item> pause() {
        paused = true;
        lineStreamer.pause();
        return this;
    }

    @Override
    public ReadStream<Item> resume() {
        paused = false;
        lineStreamer.resume();
        return this;
    }

    @Override
    public ReadStream<Item> endHandler(final Handler<Void> endHandler) {
        this.endHandler = endHandler;
        return this;
    }

    private void parse(final String line) {
        if (line.equals(endToken)) {
            commitCurrent();
            endHandler.handle(null);
            lineStreamer.pollHandler();
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
            itemHandler.handle(new Playlist(value));
        } else if ("directory".equals(key)) {
            commitCurrent();
            json = null;
            itemHandler.handle(new Directory(value));
        } else if (json != null) {
            final Song.Fields field = FIELDS_BY_MPD_NAME.get(key);
            if (field != null) {
                json.put(field.getName(), field.parse(value));
            }
        }
    }

    private void commitCurrent() {
        if (json != null) {
            itemHandler.handle(Song.fromJson(json));
        }
    }
}
