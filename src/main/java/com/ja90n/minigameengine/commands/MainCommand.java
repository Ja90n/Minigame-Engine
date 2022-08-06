package com.ja90n.minigameengine.commands;

import com.ja90n.minigameengine.MinigameEngine;
import com.ja90n.minigameengine.runnables.SendSpigotMessageRunnable;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainCommand implements SimpleCommand {

    private MinigameEngine minigameEngine;
    private JedisPool pool;

    public MainCommand(MinigameEngine minigameEngine){
        this.minigameEngine = minigameEngine;

        JedisPoolConfig poolCfg = new JedisPoolConfig();
        poolCfg.setMaxTotal(3);
        pool = new JedisPool(poolCfg, "192.168.178.16", 6379, 500);

        try (Jedis jedis = pool.getResource()) {
            System.out.println(jedis.get("spigot:gamestate:"));
        }
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)){
            invocation.source().sendMessage(Component.text("You need to be a player to use this command!", NamedTextColor.RED));
        } else {
            Player player = (Player) invocation.source();
            switch (invocation.arguments().length){
                case 0:
                    try (redis.clients.jedis.Jedis jedis = pool.getResource()) {
                        player.sendMessage(Component.text(jedis.get("spigot:gamestate:")));
                    }
                    helpCommand(player);
                    break;
                case 1:
                    switch (invocation.arguments()[0]){
                        case "leave":
                            if (minigameEngine.getArenaManager().getArena(player) != null){
                                if (minigameEngine.getPartyManager().getParty(player) == null){
                                    try (redis.clients.jedis.Jedis jedis = pool.getResource()) {
                                        if (jedis.get("spigot:removeplayer:" + minigameEngine.getArenaManager().getArena(player).getServerInfo().getName()).equals("")){
                                            jedis.set("spigot:removeplayer:" + minigameEngine.getArenaManager().getArena(player).getServerInfo().getName(),player.getUniqueId().toString());
                                            player.sendMessage(Component.text("You have left the game!",NamedTextColor.RED));
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
                            } else {
                                player.sendMessage(Component.text("You are not in a game!",NamedTextColor.RED));
                            }
                            break;
                        case "list":
                            player.sendMessage(Component.text("These are the available arenas:",NamedTextColor.BLUE));
                            for (RegisteredServer server : minigameEngine.getArenaManager().getArenas()){
                                player.sendMessage(Component.text("- ",NamedTextColor.BLUE).append(Component.text(server.getServerInfo().getName(),NamedTextColor.WHITE)));
                            }
                            break;
                        case "listplayers":
                            if (minigameEngine.getArenaManager().getArena(player) != null){
                                for (Player player1 : minigameEngine.getArenaManager().getArena(player).getPlayersConnected()){
                                    player.sendMessage(Component.text(player1.getUsername()));
                                }
                            }
                            break;
                        default:
                            helpCommand(player);
                            break;
                    }
                    break;
                case 2:
                    if (invocation.arguments()[0].equals("join")){
                        if (minigameEngine.getArenaManager().getArena(player) != null){
                            player.sendMessage(Component.text("You are already in a arena!",NamedTextColor.RED));
                        } else {
                            try (redis.clients.jedis.Jedis jedis = pool.getResource()) {
                                try {
                                    if (!jedis.get("spigot:gamestate:" + minigameEngine.getArenaManager().getArena(invocation.arguments()[1]).getServerInfo().getName()).equals("LIVE")){
                                        if (minigameEngine.getPartyManager().getParty(player) == null){
                                            player.createConnectionRequest(minigameEngine.getArenaManager().getArena(invocation.arguments()[1])).fireAndForget();
                                            player.sendMessage(Component.text("You have joined the game!",NamedTextColor.BLUE));
                                        } else {
                                            if (minigameEngine.getPartyManager().getParty(player).getPartyLeader().equals(player.getUniqueId())){
                                                minigameEngine.getPartyManager().getParty(player).joinGame(minigameEngine.getArenaManager().getArena(invocation.arguments()[1]));
                                            } else {
                                                player.sendMessage(Component.text("You can not join the game because you are not the party leader!",NamedTextColor.RED));
                                            }
                                        }
                                    } else {
                                        player.sendMessage(Component.text("The game is already active!",NamedTextColor.RED));
                                    }
                                } catch (NullPointerException e){}

                            }
                        }
                    } else {
                        helpCommand(player);
                        break;
                    }
            }
        }
    }

    public void helpCommand(Player player){
        player.sendMessage(Component.text("beter worden"));
        /*
        player.sendMessage(ChatColor.WHITE + "-----------------" + ChatColor.LIGHT_PURPLE + "=+=" + ChatColor.WHITE + "-----------------");
        player.sendMessage(ChatColor.RED + "/party join <player name>: " + ChatColor.WHITE + "Makes you join a party");
        player.sendMessage(ChatColor.GREEN + "/party leave: " + ChatColor.WHITE + "Makes you leave your party");
        player.sendMessage(ChatColor.YELLOW + "/party list: " + ChatColor.WHITE + "Lists all players in your party");
        player.sendMessage(ChatColor.AQUA + "/party help: " + ChatColor.WHITE + "Gives you all the commands");
        player.sendMessage(ChatColor.BLUE + "/party disband: " + ChatColor.WHITE + "Makes you disband your party");
        player.sendMessage(ChatColor.WHITE + "-----------------" + ChatColor.LIGHT_PURPLE + "=+=" + ChatColor.WHITE + "-----------------");

         */
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
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
