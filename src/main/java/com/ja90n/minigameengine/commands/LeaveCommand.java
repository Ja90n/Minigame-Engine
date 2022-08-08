package com.ja90n.minigameengine.commands;

import com.ja90n.minigameengine.MinigameEngine;
import com.ja90n.minigameengine.runnables.SendSpigotMessageRunnable;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LeaveCommand implements SimpleCommand {

    private MinigameEngine minigameEngine;
    private JedisPool pool;

    public LeaveCommand(MinigameEngine minigameEngine, JedisPool pool){
        this.minigameEngine = minigameEngine;
        this.pool = pool;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.source() instanceof Player){
            Player player = (Player) invocation.source();
            if (minigameEngine.getArenaManager().getArena(player) != null){
                if (minigameEngine.getPartyManager().getParty(player) == null){
                    try (redis.clients.jedis.Jedis jedis = pool.getResource()) {
                        if (jedis.get("spigot:removeplayer:" + minigameEngine.getArenaManager().getArena(player).getServerInfo().getName()).equals("")){
                            jedis.set("spigot:removeplayer:" + minigameEngine.getArenaManager().getArena(player).getServerInfo().getName(),player.getUniqueId().toString());
                            player.sendMessage(Component.text("You have left the game!", NamedTextColor.RED));
                        } else {
                            TextComponent textComponent = Component.text("You have left the game!",NamedTextColor.RED);
                            new SendSpigotMessageRunnable(minigameEngine,
                                    "spigot:removeplayer:" + minigameEngine.getArenaManager()
                                            .getArena(player).getServerInfo().getName()
                                    ,player.getUniqueId().toString(),jedis,player,textComponent);
                        }
                    }
                } else {
                    if (minigameEngine.getPartyManager().getParty(player).getPartyLeader().equals(player.getUniqueId())){
                        minigameEngine.getPartyManager().getParty(player).leaveGame(minigameEngine.getArenaManager().getArena(player));
                    } else {
                        try (redis.clients.jedis.Jedis jedis = pool.getResource()) {
                            if (jedis.get("spigot:removeplayer:" + minigameEngine.getArenaManager().getArena(player).getServerInfo().getName()).equals("")){
                                jedis.set("spigot:removeplayer:" + minigameEngine.getArenaManager().getArena(player).getServerInfo().getName(),player.getUniqueId().toString());
                                player.sendMessage(Component.text("You have left the game without your party!",NamedTextColor.RED));
                            } else {
                                TextComponent textComponent = Component.text("You have left the game without your party!",NamedTextColor.RED);
                                new SendSpigotMessageRunnable(minigameEngine,
                                        "spigot:removeplayer:" + minigameEngine.getArenaManager()
                                                .getArena(player).getServerInfo().getName()
                                        ,player.getUniqueId().toString(),jedis,player,textComponent);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> list = new ArrayList<>();
        list.add(" ");
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
