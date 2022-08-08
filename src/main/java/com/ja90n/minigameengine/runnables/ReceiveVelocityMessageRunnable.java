package com.ja90n.minigameengine.runnables;

import com.ja90n.minigameengine.MinigameEngine;
import com.ja90n.minigameengine.instances.Party;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReceiveVelocityMessageRunnable {

    private ScheduledTask scheduledTask;

    public ReceiveVelocityMessageRunnable(MinigameEngine minigameEngine, JedisPool pool) {
        try (Jedis jedis = pool.getResource()) {
            scheduledTask = minigameEngine.getServer().getScheduler()
                    .buildTask(minigameEngine, () -> {
                        for (RegisteredServer server : minigameEngine.getArenaManager().getArenas()) {
                            if (!jedis.get("velocity:removeplayer:" + server.getServerInfo().getName()).equals("")) {
                                String player = jedis.get("velocity:removeplayer:" + server.getServerInfo().getName());
                                if (minigameEngine.getServer().getPlayer(UUID.fromString(player)).isPresent()) {
                                    if (minigameEngine.getServer().getServer("lobby").isPresent()) {
                                        minigameEngine.getServer().getPlayer(UUID.fromString(player))
                                                .get().createConnectionRequest
                                                        (minigameEngine.getServer().getServer("lobby")
                                                                .get()).fireAndForget();
                                    }
                                }
                                jedis.set("velocity:removeplayer:" + server.getServerInfo().getName(),"");
                            } if (!jedis.get("velocity:addplayer:" + server.getServerInfo().getName()).equals("")){
                                String player = jedis.get("velocity:addplayer:" + server.getServerInfo().getName());
                                if (minigameEngine.getServer().getPlayer(UUID.fromString(player)).isPresent()) {
                                    Player player1 = minigameEngine.getServer().getPlayer(UUID.fromString(player)).get();
                                    if (minigameEngine.getPartyManager().getParty(player1) == null){
                                        minigameEngine.getServer().getPlayer(UUID.fromString(player))
                                                .get().createConnectionRequest
                                                        (server).fireAndForget();
                                        player1.sendMessage(Component.text("You have joined the game!", NamedTextColor.BLUE));
                                    } else {
                                        Party party = minigameEngine.getPartyManager().getParty(player1);
                                        if (party.getPartyLeader().equals(player1.getUniqueId())){
                                            party.joinGame(server);
                                        } else {
                                            player1.sendMessage(Component.text("You can not join this game because you are not the party leader!", NamedTextColor.RED));
                                        }
                                    }

                                }
                                jedis.set("velocity:addplayer:" + server.getServerInfo().getName(),"");
                            }
                        }
                    })
                    .repeat(100L, TimeUnit.MILLISECONDS)
                    .schedule();
        }
    }
}
