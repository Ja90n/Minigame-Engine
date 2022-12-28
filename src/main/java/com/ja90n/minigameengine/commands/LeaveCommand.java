/*
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
                    if (minigameEngine.getServer().getServer("lobby").isPresent()){
                        player.sendMessage(Component.text("You have left the game!",NamedTextColor.BLUE));
                        player.createConnectionRequest(minigameEngine.getServer().getServer("lobby").get()).fireAndForget();
                    }
                } else {
                    if (minigameEngine.getPartyManager().getParty(player).getPartyLeader().equals(player.getUniqueId())){
                        minigameEngine.getPartyManager().getParty(player).leaveGame(minigameEngine.getArenaManager().getArena(player));
                    } else {
                        if (minigameEngine.getServer().getServer("lobby").isPresent()){
                            player.sendMessage(Component.text("You have left the game without your party!",NamedTextColor.BLUE));
                            player.createConnectionRequest(minigameEngine.getServer().getServer("lobby").get()).fireAndForget();
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

 */
