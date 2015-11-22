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

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class WebVerticle extends AbstractVerticle {

    public static final String NAME = WebVerticle.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(WebVerticle.class);
    private static final List<String> TEXT = ImmutableList.of(
            "This site",
            "will be about",
            "Music Player Daemon",
            "with Vert.x !",
            "...",
            "Please, be patient."
    );

    @Override
    public void start() throws Exception {
        LOGGER.info("Starting WebVerticle");

        final Router router = Router.router(vertx);
        router.route("/eventbus/*").handler(SockJSHandler.create(vertx)
                .bridge(new BridgeOptions()
                        .addOutboundPermitted(new PermittedOptions().setAddress("info"))
                        .addInboundPermitted(new PermittedOptions().setAddress("init"))));

        router.route().handler(StaticHandler.create());

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        vertx.eventBus().consumer("init").handler(evt -> {
            final Iterator<String> itLines = TEXT.iterator();
            final AtomicLong hPeriodic = new AtomicLong(0);
            hPeriodic.set(vertx.setPeriodic(2000, l -> {
                if (itLines.hasNext()) {
                    final String line = itLines.next();
                    LOGGER.info(line);
                    vertx.eventBus().publish("info", new JsonObject().put("line", line));
                } else {
                    LOGGER.info("Stop periodic");
                    vertx.eventBus().publish("info", new JsonObject().put("line", "STOP."));
                    vertx.cancelTimer(hPeriodic.get());
                }
            }));
        });
    }
}
