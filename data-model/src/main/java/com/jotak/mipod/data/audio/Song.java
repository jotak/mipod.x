package com.jotak.mipod.data.audio;

import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class Song implements Item {

    private final Optional<String> filepath;
    private final Optional<String> lastModified;
    private final Optional<Long> time;
    private final Optional<String> artist;
    private final Optional<String> albumArtist;
    private final Optional<String> title;
    private final Optional<String> album;
    private final Optional<String> track;
    private final Optional<String> date;
    private final Optional<String> genre;
    private final Optional<String> composer;
    private final Optional<Integer> pos;
    private final Optional<Integer> id;

    private Song(
                 final Optional<String> filepath,
                 final Optional<String> lastModified,
                 final Optional<Long> time,
                 final Optional<String> artist,
                 final Optional<String> albumArtist,
                 final Optional<String> title,
                 final Optional<String> album,
                 final Optional<String> track,
                 final Optional<String> date,
                 final Optional<String> genre,
                 final Optional<String> composer,
                 final Optional<Integer> pos,
                 final Optional<Integer> id) {
        this.filepath = filepath;
        this.lastModified = lastModified;
        this.time = time;
        this.artist = artist;
        this.albumArtist = albumArtist;
        this.title = title;
        this.album = album;
        this.track = track;
        this.date = date;
        this.genre = genre;
        this.composer = composer;
        this.pos = pos;
        this.id = id;
    }

    public Optional<String> getFilepath() {
        return filepath;
    }

    public Optional<String> getLastModified() {
        return lastModified;
    }

    public Optional<Long> getTime() {
        return time;
    }

    public Optional<String> getArtist() {
        return artist;
    }

    public Optional<String> getAlbumArtist() {
        return albumArtist;
    }

    public Optional<String> getTitle() {
        return title;
    }

    public Optional<String> getAlbum() {
        return album;
    }

    public Optional<String> getTrack() {
        return track;
    }

    public Optional<String> getDate() {
        return date;
    }

    public Optional<String> getGenre() {
        return genre;
    }

    public Optional<String> getComposer() {
        return composer;
    }

    public Optional<Integer> getPos() {
        return pos;
    }

    public Optional<Integer> getId() {
        return id;
    }

    public JsonObject toJson() {
        final JsonObject jsonObject = new JsonObject();
        Fields.asList().forEach(
                field -> field.getValue(this).ifPresent(v -> jsonObject.put(field.getName(), v)));
        return jsonObject;
    }

    public static Song fromJson(final JsonObject jsonObject) {
        return new Song(
                Optional.of(jsonObject.getString(Fields.FILEPATH.getName())),
                Optional.ofNullable(jsonObject.getString(Fields.LASTMODIFIED.getName())),
                Optional.ofNullable(jsonObject.getLong(Fields.TIME.getName())),
                Optional.ofNullable(jsonObject.getString(Fields.ARTIST.getName())),
                Optional.ofNullable(jsonObject.getString(Fields.ALBUMARTIST.getName())),
                Optional.ofNullable(jsonObject.getString(Fields.TITLE.getName())),
                Optional.ofNullable(jsonObject.getString(Fields.ALBUM.getName())),
                Optional.ofNullable(jsonObject.getString(Fields.TRACK.getName())),
                Optional.ofNullable(jsonObject.getString(Fields.DATE.getName())),
                Optional.ofNullable(jsonObject.getString(Fields.GENRE.getName())),
                Optional.ofNullable(jsonObject.getString(Fields.COMPOSER.getName())),
                Optional.ofNullable(jsonObject.getInteger(Fields.POS.getName())),
                Optional.ofNullable(jsonObject.getInteger(Fields.ID.getName())));
    }

    public enum Fields {
        FILEPATH("filepath", "file", Song::getFilepath, String::valueOf),
        LASTMODIFIED("lastModified", "Last-Modified", Song::getLastModified, String::valueOf),
        TIME("time", "Time", Song::getTime, Long::valueOf),
        ARTIST("artist", "Artist", Song::getArtist, String::valueOf),
        ALBUMARTIST("albumArtist", "AlbumArtist", Song::getAlbumArtist, String::valueOf),
        TITLE("title", "Title", Song::getTitle, String::valueOf),
        ALBUM("album", "Album", Song::getAlbum, String::valueOf),
        TRACK("track", "Track", Song::getTrack, String::valueOf),
        DATE("date", "Date", Song::getDate, String::valueOf),
        GENRE("genre", "Genre", Song::getGenre, String::valueOf),
        COMPOSER("composer", "Composer", Song::getComposer, String::valueOf),
        POS("pos", "Pos", Song::getPos, Integer::valueOf),
        ID("id", "Id", Song::getId, Integer::valueOf);

        private final String name;
        private final String mpdName;
        private final Function<Song, Optional<?>> valueSupplier;
        private final Function<String, ?> valueParser;

        Fields(final String name, final String mpdName, final Function<Song, Optional<?>> valueSupplier, final Function<String, ?> valueParser) {
            this.name = name;
            this.mpdName = mpdName;
            this.valueSupplier = valueSupplier;
            this.valueParser = valueParser;
        }
        public String getName() {
            return name;
        }
        public String getMpdName() {
            return mpdName;
        }
        public Optional<?> getValue(final Song song) {
            return valueSupplier.apply(song);
        }
        public Object parse(final String strValue) {
            return valueParser.apply(strValue);
        }
        public static List<Fields> asList() {
            return Arrays.asList(values());
        }
    }
}
