package com.jotak.mipod.configuration;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class MpdClientConfiguration {
    private final String hostname;
    private final int port;

    public MpdClientConfiguration(final String hostname, final int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }
}
