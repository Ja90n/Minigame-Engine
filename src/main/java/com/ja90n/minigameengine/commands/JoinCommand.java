/*
package com.ja90n.minigameengine.commands;

import com.ja90n.minigameengine.MinigameEngine;
import com.ja90n.minigameengine.instances.Party;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JoinCommand implements SimpleCommand {

    private MinigameEngine minigameEngine;
    private JedisPool pool;

    public JoinCommand(MinigameEngine minigameEngine, JedisPool pool){
        this.minigameEngine = minigameEngine;
        this.pool = pool;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.source() instanceof Player){
            Player player = (Player) invocation.source();
            if (minigameEngine.getArenaManager().getArena(player) != null){
                player.sendMessage(Component.text("You are already in a arena!", NamedTextColor.RED));
            } else {
                try (redis.clients.jedis.Jedis jedis = pool.getResource()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("arena0").append(invocation.arguments()[0]);
                    if (minigameEngine.getArenaManager().getArena(stringBuilder.toString()) != null){
                        if (!jedis.get("spigot:gamestate:" + minigameEngine.getArenaManager().getArena(stringBuilder.toString()).getServerInfo().getName()).equals("LIVE")){
                            if (minigameEngine.getServer().getServer(invocation.arguments()[0]).isPresent()){
                                RegisteredServer server = minigameEngine.getServer().getServer(invocation.arguments()[0]).get();
                                if (minigameEngine.getPartyManager().getParty(player) == null){
                                    player.createConnectionRequest(server).fireAndForget();
                                } else {
                                    Party party = minigameEngine.getPartyManager().getParty(player);
                                    if (party.getPartyLeader().equals(player.getUniqueId())){
                                        party.joinGame(server);
                                    } else {
                                        player.sendMessage(Component.text("You can not join this game because you are not the party leader!", NamedTextColor.RED));
                                    }
                                }
                            }
                        } else {
                            player.sendMessage(Component.text("The game you tried to join is already active!",NamedTextColor.RED));
                        }
                    } else {
                        player.sendMessage(Component.text("The game you tried to join does not exist!",NamedTextColor.RED));
                    }
                }
            }
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> list = new ArrayList<>();
        if (invocation.arguments().length == 0 || invocation.arguments().length == 1){
            for (RegisteredServer server : minigameEngine.getArenaManager().getArenas()){
                StringBuilder stringBuilder = new StringBuilder(server.getServerInfo().getName());
                stringBuilder.delete(0,6);
                list.add(stringBuilder.toString());
            }
        }
        return list;
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return SimpleCommand.super.suggestAsync(invocation);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return SimpleCommand.super.hasPermission(invocation);
    }
}

 */
