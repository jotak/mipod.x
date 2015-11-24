package com.jotak.mipod.data.audio;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class Playlist implements Item {
    private final String name;

    public Playlist(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
