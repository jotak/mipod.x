package com.jotak.mipod.web;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public final class WebEventBusAddresses {
    private static final String PREFIX = "WEB_";
    public static final String INIT_CLIENT_CONN = PREFIX + "InitConnection";
    public static final String AUDIO_PLAY = PREFIX + "AudioPlay";
    public static final String AUDIO_STOP = PREFIX + "AudioStop";
    public static final String AUDIO_PAUSE = PREFIX + "AudioPause";
    public static final String AUDIO_PREV = PREFIX + "AudioPrev";
    public static final String AUDIO_NEXT = PREFIX + "AudioNext";
    public static final String INFO_MESSAGE = PREFIX + "Info";
    public static final String AUDIO_CURRENT = PREFIX + "Current";
}
