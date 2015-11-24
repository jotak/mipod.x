package com.jotak.mipod.data.audio;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class Directory implements Item {
    private final String name;

    public Directory(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
