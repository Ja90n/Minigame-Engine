/*
package com.ja90n.minigameengine.commands;

import com.ja90n.minigameengine.MinigameEngine;
import com.ja90n.minigameengine.instances.Party;
import com.ja90n.minigameengine.managers.PartyManager;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PartyCommand implements SimpleCommand {

    private MinigameEngine minigameEngine;
    private PartyManager partyManager;

    public PartyCommand(MinigameEngine minigameEngine) {
        this.minigameEngine = minigameEngine;
        partyManager = minigameEngine.getPartyManager();
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.source() instanceof Player){
            Player player = (Player) invocation.source();
            switch (invocation.arguments().length){
                case 0:
                    helpCommand(player);
                    break;
                case 1:
                    switch (invocation.arguments()[0]){
                        case "leave":
                            partyManager.removePlayer(player);
                            player.sendMessage(Component.text("You have left the party!", NamedTextColor.BLUE));
                            break;
                        case "list":
                            partyManager.listParty(player);
                            break;
                        case "disband":
                            if (partyManager.getParty(player) != null){
                                if (partyManager.getParty(player).getPartyLeader().equals(player.getUniqueId())){
                                    partyManager.disbandParty(partyManager.getParty(player));
                                    player.sendMessage(Component.text("You disbanded the party!",NamedTextColor.BLUE));
                                } else {
                                    player.sendMessage(Component.text("You are not the party leader!",NamedTextColor.RED));
                                }
                            } else {
                                player.sendMessage(Component.text("You are not in a party!",NamedTextColor.RED));
                            }
                            break;
                        case "create":
                            if (partyManager.getParty(player) == null){
                                partyManager.newParty(player);
                                player.sendMessage(Component.text("You have created a new party!",NamedTextColor.BLUE));
                            } else {
                                player.sendMessage(Component.text("You are already in a party!",NamedTextColor.RED));
                            }
                            break;
                        case "listp":
                            for (UUID uuid : partyManager.getPlayers()){
                                if (minigameEngine.getServer().getPlayer(uuid).isPresent()){
                                    player.sendMessage(Component.text(minigameEngine.getServer().getPlayer(uuid).get().getUsername()));
                                }
                            }
                            break;
                        default:
                            helpCommand(player);
                            break;
                    }
                    break;
                case 2:
                    if (invocation.arguments()[0].equalsIgnoreCase("join")){
                        if (partyManager.getParty(player) == null){
                            boolean isFound = false;
                            for (Party party : partyManager.getParties()){
                                for (UUID uuid : party.getPlayers()){
                                    if (minigameEngine.getServer().getPlayer(uuid).isPresent()){
                                        if (invocation.arguments()[1].equalsIgnoreCase(minigameEngine.getServer().getPlayer(uuid).get().getUsername())){
                                            party.addPlayer(player);
                                            TextComponent textComponent = Component.text("Player ",NamedTextColor.BLUE)
                                                    .append(Component.text(player.getUsername(),NamedTextColor.WHITE))
                                                    .append(Component.text(" has been added to the party!",NamedTextColor.BLUE));
                                            partyManager.sendMessage(party,textComponent);
                                            isFound = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!isFound){
                                player.sendMessage(Component.text("The party you are trying to join does not exist!",NamedTextColor.RED));
                            }
                        } else {
                            player.sendMessage(Component.text("You are already in a party!",NamedTextColor.RED));
                        }
                    } else if (invocation.arguments()[0].equalsIgnoreCase("invite")){
                        if (partyManager.getParty(player) != null){
                            inviteToParty(player,invocation);
                        } else {
                            minigameEngine.getPartyManager().newParty(player);
                            player.sendMessage(Component.text("Created new party",NamedTextColor.BLUE));
                            inviteToParty(player,invocation);
                        }
                    } else if (invocation.arguments()[0].equalsIgnoreCase("promote")) {
                        if (partyManager.getParty(player) != null){
                            if (partyManager.getParty(player).getPartyLeader().equals(player.getUniqueId())){
                                if (minigameEngine.getServer().getPlayer(invocation.arguments()[1]).isPresent()){
                                    if (partyManager.getParty(player).getPlayers().contains(minigameEngine.getServer().getPlayer(invocation.arguments()[1]).get().getUniqueId())){
                                        partyManager.getParty(player).setPartyLeader(minigameEngine.getServer().getPlayer(invocation.arguments()[1]).get().getUniqueId());
                                    } else {
                                        player.sendMessage(Component.text("That player is not in your party!",NamedTextColor.RED));
                                    }
                                }
                            } else {
                                player.sendMessage(Component.text("You are not the party leader of your party!",NamedTextColor.RED));
                            }
                        } else {
                            player.sendMessage(Component.text("You are not in a party!",NamedTextColor.RED));
                        }
                    } else {
                        helpCommand(player);
                    }
                    break;
                default:
                    helpCommand(player);
                    break;
            }
        } else {
            invocation.source().sendMessage(Component.text("You need to be a player to use this command!",NamedTextColor.RED));
        }
    }

    public void inviteToParty(Player player, Invocation invocation){
        if (!invocation.arguments()[1].equals(player.getUsername())){
            boolean foundParty = false;
            for (Player target : minigameEngine.getServer().getAllPlayers()){
                if (invocation.arguments()[1].equals(target.getUsername())){
                    TextComponent textComponent = Component.text("You have been invited to ",NamedTextColor.BLUE)
                            .append(Component.text(minigameEngine.getServer()
                                            .getPlayer(partyManager.getParty(player)
                                                    .getPartyLeader()).get().getUsername(),NamedTextColor.WHITE)
                                    .append(Component.text("'s"))
                                    .append(Component.text(" party!", NamedTextColor.BLUE)));
                    TextComponent textComponent1 = Component.text("Click ",NamedTextColor.BLUE);
                    TextComponent textComponent2 = Component.text("HERE",NamedTextColor.WHITE)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,"/p join " + player.getUsername()))
                            .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,Component.text("Join the party of ",NamedTextColor.BLUE).append(Component.text(player.getUsername()))));
                    TextComponent textComponent3 = Component.text(" to join the party!",NamedTextColor.BLUE);
                    TextComponent textComponent4 = textComponent1.append(textComponent2.append(textComponent3));
                    target.sendMessage(textComponent);
                    target.sendMessage(textComponent4);
                    foundParty = true;
                    player.sendMessage(Component.text("Invite send to ", NamedTextColor.BLUE)
                            .append(Component.text(target.getUsername(),NamedTextColor.WHITE))
                            .append(Component.text("!",NamedTextColor.BLUE)));
                    break;
                }
            }
            if (!foundParty){
                player.sendMessage(Component.text("There is no player online with that name!",NamedTextColor.RED));
            }
        } else {
            player.sendMessage(Component.text("You can't invite yourself!",NamedTextColor.RED));
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


    }


    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> list = new ArrayList<>();
        if (invocation.source() instanceof Player){
            Player player = (Player) invocation.source();
            switch (invocation.arguments().length){
                case 0:
                case 1:
                    list.add("join");
                    list.add("leave");
                    list.add("create");
                    list.add("invite");
                    list.add("list");
                    list.add("help");
                    list.add("promote");
                    list.add("disband");
                    break;
                case 2:
                    if (invocation.arguments()[0].equalsIgnoreCase("join")){
                        for (Player player1 : minigameEngine.getServer().getAllPlayers()){
                            list.add(player1.getUsername());
                        }
                    } else if (invocation.arguments()[0].equalsIgnoreCase("invite")){
                        for (Player player1 : minigameEngine.getServer().getAllPlayers()){
                            list.add(player1.getUsername());
                        }
                    } else if (invocation.arguments()[0].equalsIgnoreCase("promote")){
                        for (Player player1 : minigameEngine.getServer().getAllPlayers()){
                            list.add(player1.getUsername());
                        }
                    }
                    break;
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
