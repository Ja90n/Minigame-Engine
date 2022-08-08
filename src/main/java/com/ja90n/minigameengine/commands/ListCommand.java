package com.ja90n.minigameengine.commands;

import com.ja90n.minigameengine.MinigameEngine;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ListCommand implements SimpleCommand {

    private MinigameEngine minigameEngine;

    public ListCommand(MinigameEngine minigameEngine){
        this.minigameEngine = minigameEngine;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.arguments().length == 1){
            if (invocation.arguments()[0].equals("players")){
                invocation.source().sendMessage(Component.text("These are the online players:",NamedTextColor.BLUE));
                for (Player player : minigameEngine.getServer().getAllPlayers()){
                    invocation.source().sendMessage(Component.text(player.getUsername()));
                }
            } else if (invocation.arguments()[0].equals("arenas")){
                invocation.source().sendMessage(Component.text("These are the available arenas:",NamedTextColor.BLUE));
                for (RegisteredServer server : minigameEngine.getArenaManager().getArenas()){
                    StringBuilder stringBuilder = new StringBuilder(server.getServerInfo().getName());
                    stringBuilder.delete(0,6);
                    invocation.source().sendMessage(Component.text("- ", NamedTextColor.BLUE).append(Component.text(stringBuilder.toString(),NamedTextColor.WHITE)));
                }
            }
        } else {
            invocation.source().sendMessage(Component.text("Usage: /list players/arenas"));
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> list = new ArrayList<>();
        if (invocation.arguments().length == 0 || invocation.arguments().length == 1){
            list.add("players");
            list.add("arenas");
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
