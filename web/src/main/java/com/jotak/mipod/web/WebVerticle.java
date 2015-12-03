package com.jotak.mipod.web;

import com.google.common.collect.ImmutableList;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.jotak.mipod.web.WebEventBusAddresses.*;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class WebVerticle extends AbstractVerticle {

    public static final String NAME = WebVerticle.class.getName();

    private static final Logger LOGGER = LoggerFactory.getLogger(WebVerticle.class);
    private static final List<String> JS_EVENTBUS_IN = ImmutableList.of(
            INIT_CLIENT_CONN,
            AUDIO_PLAY,
            AUDIO_STOP,
            AUDIO_NEXT,
            AUDIO_PREV
    );
    private static final List<String> JS_EVENTBUS_OUT = ImmutableList.of(
            INFO_MESSAGE,
            AUDIO_CURRENT
    );
    private static final List<String> TEXT = ImmutableList.of(
//            "This site",
//            "will be about",
//            "Music Player Daemon",
//            "with Vert.x !",
//            "...",
//            "Please, be patient."
    );

    public WebVerticle() {
        super();
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("Starting WebVerticle");

        final Router router = Router.router(vertx);
        final BridgeOptions bridgeOptions = new BridgeOptions();
        JS_EVENTBUS_IN.stream().map(str -> new PermittedOptions().setAddress(str)).forEach(bridgeOptions::addInboundPermitted);
        JS_EVENTBUS_OUT.stream().map(str -> new PermittedOptions().setAddress(str)).forEach(bridgeOptions::addOutboundPermitted);
        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(bridgeOptions));

        router.route().handler(StaticHandler.create());

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        vertx.eventBus().consumer(INIT_CLIENT_CONN).handler(evt -> postInit());
    }

    private void postInit() {
        // Register audio dispatcher events on EventBus
        AudioDispatcher.registerEvents(vertx.eventBus());

        final Iterator<String> itLines = TEXT.iterator();
        final AtomicLong hPeriodic = new AtomicLong(0);
        hPeriodic.set(vertx.setPeriodic(2000, l -> {
            if (itLines.hasNext()) {
                final String line = itLines.next();
                LOGGER.info(line);
                vertx.eventBus().publish(INFO_MESSAGE, new JsonObject().put("line", line));
            } else {
                LOGGER.info("Stop periodic");
                vertx.eventBus().publish(INFO_MESSAGE, new JsonObject().put("line", "STOP."));
                vertx.cancelTimer(hPeriodic.get());
            }
        }));

        // Register audio consuming events on EventBus
        AudioConsumers.listenCurrentTrack(vertx.eventBus());
    }
}
