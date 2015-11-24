package com.jotak.mipod.mpd.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.jotak.mipod.data.audio.Directory;
import com.jotak.mipod.data.audio.Item;
import com.jotak.mipod.data.audio.Playlist;
import com.jotak.mipod.data.audio.Song;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public final class MpdParser {

    public static List<Item> parseEntries(final List<String> lines) {
        final ImmutableList.Builder<Item> listBuilder = ImmutableList.builder();
        JsonObject json = null;
        final Map<String, Song.Fields> fieldsByMpdName = Maps.uniqueIndex(Song.Fields.asList(), Song.Fields::getMpdName);
        for (final String line : lines) {
            final String[] entry = line.split(": ", 2);
            final String key = entry[0];
            final String value = entry.length > 1 ? entry[1] : "";
            if ("file".equals(key)) {
                commitEntry(json, listBuilder);
                json = new JsonObject().put(Song.Fields.FILEPATH.getName(), value);
            } else if ("playlist".equals(key)) {
                commitEntry(json, listBuilder);
                json = null;
                listBuilder.add(new Playlist(value));
            } else if ("directory".equals(key)) {
                commitEntry(json, listBuilder);
                json = null;
                listBuilder.add(new Directory(value));
            } else if (json != null) {
                final Song.Fields field = fieldsByMpdName.get(key);
                if (field != null) {
                    json.put(field.getName(), field.parse(value));
                }
            }
        }
        commitEntry(json, listBuilder);
        return listBuilder.build();
    }

    private static void commitEntry(final JsonObject json, final ImmutableList.Builder<Item> listBuilder) {
        if (json != null) {
            listBuilder.add(Song.fromJson(json));
        }
    }
}
