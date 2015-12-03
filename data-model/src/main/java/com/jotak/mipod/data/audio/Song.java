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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Song song = (Song) o;

        if (filepath != null ? !filepath.equals(song.filepath) : song.filepath != null) return false;
        if (lastModified != null ? !lastModified.equals(song.lastModified) : song.lastModified != null) return false;
        if (time != null ? !time.equals(song.time) : song.time != null) return false;
        if (artist != null ? !artist.equals(song.artist) : song.artist != null) return false;
        if (albumArtist != null ? !albumArtist.equals(song.albumArtist) : song.albumArtist != null) return false;
        if (title != null ? !title.equals(song.title) : song.title != null) return false;
        if (album != null ? !album.equals(song.album) : song.album != null) return false;
        if (track != null ? !track.equals(song.track) : song.track != null) return false;
        if (date != null ? !date.equals(song.date) : song.date != null) return false;
        if (genre != null ? !genre.equals(song.genre) : song.genre != null) return false;
        if (composer != null ? !composer.equals(song.composer) : song.composer != null) return false;
        if (pos != null ? !pos.equals(song.pos) : song.pos != null) return false;
        return !(id != null ? !id.equals(song.id) : song.id != null);

    }

    @Override
    public int hashCode() {
        int result = filepath != null ? filepath.hashCode() : 0;
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        result = 31 * result + (albumArtist != null ? albumArtist.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (album != null ? album.hashCode() : 0);
        result = 31 * result + (track != null ? track.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (genre != null ? genre.hashCode() : 0);
        result = 31 * result + (composer != null ? composer.hashCode() : 0);
        result = 31 * result + (pos != null ? pos.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
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
