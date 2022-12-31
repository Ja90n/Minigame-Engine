package com.ja90n.minigameengine.commands;

import com.ja90n.minigameengine.MinigameEngine;
import com.ja90n.minigameengine.instances.Party;
import com.ja90n.minigameengine.managers.PartyManager;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.Optional;

public class PartyCommand implements SimpleCommand {

    private MinigameEngine minigameEngine;
    private PartyManager partyManager;

    public PartyCommand(MinigameEngine minigameEngine) {
        this.minigameEngine = minigameEngine;
        partyManager = minigameEngine.getPartyManager();
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)){
            invocation.source().sendMessage(Component.text("You need to be a player to use this command!", NamedTextColor.RED));
            return;
        }

        Player player = (Player) invocation.source();
        String[] args = invocation.arguments();

        switch (args.length){
            case 1:
                // If there is one argument
                switch (args[0]){
                    case "leave":
                        leaveParty(player);
                        break;
                    case "listplayers":
                    case "listp":
                    case "list":
                        listPlayers(player);
                        break;
                    case "disband":
                        disbandParty(player);
                        break;
                    case "create":
                        createParty(player);
                        break;
                    default:
                        // If the argument is not a standard argument try to invite the player from the name
                        invitePlayer(player,args[0]);
                        break;
                }
                break;
            case 2:
                // If there are two arguments
                switch (args[0]){
                    case "join":
                        joinParty(player,args[1]);
                        break;
                    case "invite":
                        invitePlayer(player,args[1]);
                        break;
                    case "promote":

                        break;
                }
        }

    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }

    public void joinParty(Player player, String targetName){
        if (partyManager.getParty(player) != null){
            player.sendMessage(Component.text("You are already in a party!",NamedTextColor.RED));
            return;
        }
        Party party = partyManager.getParty(targetName);
        if (party == null){
            player.sendMessage(Component.text("The party you are trying to join does not exist!", NamedTextColor.RED));
            return;
        }
        if (!party.getInvitedPlayers().contains(player.getUniqueId())){
            player.sendMessage(Component.text("You have not been invited to that party!",NamedTextColor.RED));
            return;
        }
        party.addPlayer(player);
        TextComponent message = Component.text(player.getUsername())
                .append(Component.text(" has joined the party!", NamedTextColor.BLUE));
        partyManager.sendMessage(party,message);
    }

    public void promotePlayer(Player player, String targetName){

    }

    public void createParty(Player player){
        if (partyManager.getParty(player) != null) {
            player.sendMessage(Component.text("You are already in a party!",NamedTextColor.RED));
            return;
        }
        partyManager.newParty(player);
        player.sendMessage(Component.text("You have created a new party!",NamedTextColor.BLUE));
    }

    public void invitePlayer(Player player, String targetName){
        Party party = partyManager.getParty(player);
        if (party == null){
            player.sendMessage(Component.text("You are not in a party!",NamedTextColor.RED));
            return;
        }
        Player target = null;
        for (Player targetPlayer : minigameEngine.getServer().getAllPlayers()){
            if (targetPlayer.getUsername().equals(targetName)){
                target = targetPlayer;
            }
        }
        if (target == null){
            TextComponent textComponent = Component.text("Player ",NamedTextColor.RED)
                    .append(Component.text(targetName, NamedTextColor.WHITE))
                    .append(Component.text(" is not online!",NamedTextColor.RED));

            player.sendMessage(textComponent);
            return;
        }
        partyManager.invitePlayer(player,target);

        player.sendMessage(Component.text("You have send an invite to ",NamedTextColor.BLUE)
                .append(Component.text(targetName))
                .append(Component.text("!",NamedTextColor.BLUE)));
    }

    public void disbandParty(Player player){
        Party party = partyManager.getParty(player);
        if (party == null) {
            player.sendMessage(Component.text("You are not in a party!", NamedTextColor.RED));
            return;
        }
        if (!party.getPartyLeader().equals(player.getUniqueId())){
            player.sendMessage(Component.text("You are not the leader of your party!", NamedTextColor.RED));
            return;
        }
        partyManager.disbandParty(party);
        player.sendMessage(Component.text("You have disbanded your party!", NamedTextColor.BLUE));
    }

    public void listPlayers(Player player){
        partyManager.listParty(player);
    }

    public void leaveParty(Player player){
        partyManager.removePlayer(player);
        player.sendMessage(Component.text("You have left the party!", NamedTextColor.BLUE));
    }
}