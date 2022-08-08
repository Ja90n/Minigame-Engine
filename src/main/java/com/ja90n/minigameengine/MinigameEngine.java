package com.ja90n.minigameengine;

import com.google.inject.Inject;
import com.ja90n.minigameengine.commands.JoinCommand;
import com.ja90n.minigameengine.commands.LeaveCommand;
import com.ja90n.minigameengine.commands.ListCommand;
import com.ja90n.minigameengine.commands.PartyCommand;
import com.ja90n.minigameengine.events.PlayerDisconnect;
import com.ja90n.minigameengine.managers.ArenaManager;
import com.ja90n.minigameengine.managers.PartyManager;
import com.ja90n.minigameengine.runnables.ReceiveVelocityMessageRunnable;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Plugin(
        id = "minigame-engine",
        name = "Minigame Engine",
        version = "0.1.0"
)
public class MinigameEngine {

    private final ProxyServer server;
    private final Logger logger;
    private CommandManager commandManager;
    private PartyManager partyManager;
    private ArenaManager arenaManager;
    private JedisPool pool;
    private ReceiveVelocityMessageRunnable receiveVelocityMessageRunnable;

    @Inject
    public MinigameEngine(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        commandManager = server.getCommandManager();
        partyManager = new PartyManager(this);
        arenaManager = new ArenaManager(this);
        logger.info("Thank you for using my system! - Ja90n");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getEventManager().register(this, new PlayerDisconnect(this));

        JedisPoolConfig poolCfg = new JedisPoolConfig();
        poolCfg.setMaxTotal(3);

        pool = new JedisPool(poolCfg, "192.168.178.16", 6379, 500);

        commandManager.register("party",new PartyCommand(this));
        commandManager.register("p",new PartyCommand(this));

        //commandManager.register("join",new JoinCommand(this,pool));
        commandManager.register("leave",new LeaveCommand(this,pool));
        commandManager.register("list",new ListCommand(this));

        receiveVelocityMessageRunnable = new ReceiveVelocityMessageRunnable(this,pool);
    }

    public ProxyServer getServer() {
        return server;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }
}
