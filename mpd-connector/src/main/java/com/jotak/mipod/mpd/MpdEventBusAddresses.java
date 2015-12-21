package com.jotak.mipod.mpd;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public final class MpdEventBusAddresses {
    private static final String PREFIX = "MPD_";
    public static final String TRACK_CHANGED = PREFIX + "TrackChanged";
    public static final String GET_CURRENT_TRACK = PREFIX + "CurrentTrack";
    public static final String PLAY = PREFIX + "Play";
    public static final String STOP = PREFIX + "Stop";
    public static final String PAUSE = PREFIX + "Pause";
    public static final String PREV = PREFIX + "Prev";
    public static final String NEXT = PREFIX + "Next";
}
