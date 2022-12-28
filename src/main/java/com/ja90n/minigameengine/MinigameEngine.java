package com.ja90n.minigameengine;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(
        id = "minigame-engine",
        name = "MinigameEngine",
        version = "0.1.0",
        description = "Hello there",
        authors = {"Ja90n"}
)

public class MinigameEngine {

    private final ProxyServer server;
    private final Logger logger;
    private ServerCommunicationHandler serverCommunicationHandler;

    @Inject
    public MinigameEngine(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        serverCommunicationHandler = new ServerCommunicationHandler(this);
        logger.info("Thank you for using my system! - Ja90n");
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

}
