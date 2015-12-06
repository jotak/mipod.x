package com.jotak.mipod.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
final class Logging {
    static void initialize() {
        final InputStream inputStream = Logging.class.getResourceAsStream("/logging.properties");
        try
        {
            LogManager.getLogManager().readConfiguration(inputStream);
        }
        catch (final IOException e)
        {
            Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
            Logger.getAnonymousLogger().severe(e.getMessage());
        }
    }
}
