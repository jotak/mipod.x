package com.jotak.mipod.mpd;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class MpdVerticle extends AbstractVerticle {

    public static final String NAME = MpdVerticle.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(MpdVerticle.class);

    @Override
    public void start() throws Exception {
        LOGGER.info("Starting MpdVerticle");
    }
}
