package com.ja90n.minigameengine.commands;

import com.ja90n.minigameengine.MinigameEngine;
import com.ja90n.minigameengine.instances.Party;
import com.ja90n.minigameengine.managers.PartyManager;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                        return;
                    case "listplayers":
                    case "listp":
                    case "list":
                        listPlayers(player);
                        return;
                    case "disband":
                        disbandParty(player);
                        return;
                    case "create":
                        createParty(player);
                        return;
                    case "warp":
                        warpParty(player);
                        return;
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
                        return;
                    case "invite":
                        invitePlayer(player,args[1]);
                        return;
                    case "promote":
                        promotePlayer(player,args[1]);
                        return;
                    default:
                        helpCommand(player);
                        break;
                }
            default:
                helpCommand(player);
                break;
        }

    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> list = new ArrayList<>();
        if (invocation.source() instanceof Player){
            switch (invocation.arguments().length){
                case 1:
                    list.add("join");
                    list.add("leave");
                    list.add("create");
                    list.add("invite");
                    list.add("list");
                    list.add("help");
                    list.add("promote");
                    list.add("disband");
                    list.add("warp");
                    break;
                case 2:
                    switch (invocation.arguments()[0].toLowerCase()){
                        case "join":
                        case "promote":
                        case "invite":
                            for (Player player : minigameEngine.getServer().getAllPlayers()){
                                list.add(player.getUsername());
                            }
                            break;
                    }
            }
        }
        return list;
    }

    public void warpParty(Player player){
        Party party = partyManager.getParty(player);
        if (!partyLeaderCheck(player,party)) return;
        if (player.getCurrentServer().isEmpty()) return;
        RegisteredServer registeredServer = player.getCurrentServer().get().getServer();
        for (UUID targetUUID : party.getPlayers()){
            if (targetUUID.equals(player.getUniqueId())) return;
            Optional<Player> playerOptional = minigameEngine.getServer().getPlayer(targetUUID);
            if (playerOptional.isEmpty()) return;
            Player target = playerOptional.get();
            if (!target.getCurrentServer().get().getServer().equals(registeredServer)) return;
            target.createConnectionRequest(registeredServer).fireAndForget();
            target.sendMessage(Component.text("You have been warped by the leader!", NamedTextColor.BLUE));
        }
        player.sendMessage(Component.text("You have warped your party!", NamedTextColor.BLUE));
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

    public void helpCommand(Player player){
        player.sendMessage(Component.text("beter worden"));
    }

    public void promotePlayer(Player player, String targetName){
        Party party = partyManager.getParty(player);
        if (player.getUsername().equals(targetName)){
            player.sendMessage(Component.text("You can't promote yourself!",NamedTextColor.RED));
            return;
        }
        if (!partyLeaderCheck(player,party)) return;
        Optional<Player> optionalTarget = minigameEngine.getServer().getPlayer(targetName);
        if (optionalTarget.isEmpty()){
            player.sendMessage(Component.text("Player you tried to promote is not online!", NamedTextColor.RED));
            return;
        }
        Player target = optionalTarget.get();
        partyManager.changeLeader(target,party);
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
        if (player.getUsername().equals(targetName)){
            player.sendMessage(Component.text("You can't invite yourself!",NamedTextColor.RED));
            return;
        }
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
        partyManager.invitePlayer(player,target,party);
        player.sendMessage(Component.text("You have send an invite to ",NamedTextColor.BLUE)
                .append(Component.text(targetName,NamedTextColor.WHITE))
                .append(Component.text("!",NamedTextColor.BLUE)));
    }

    public void disbandParty(Player player){
        Party party = partyManager.getParty(player);
        if (!partyLeaderCheck(player,party)) return;
        partyManager.disbandParty(party);
        player.sendMessage(Component.text("You have disbanded your party!", NamedTextColor.BLUE));
    }

    public boolean partyLeaderCheck(Player player, Party party){
        if (party == null) {
            player.sendMessage(Component.text("You are not in a party!", NamedTextColor.RED));
            return false;
        }
        if (!party.getPartyLeader().equals(player.getUniqueId())){
            player.sendMessage(Component.text("You are not the leader of your party!", NamedTextColor.RED));
            return false;
        }
        return true;
    }

    public void listPlayers(Player player){
        partyManager.listParty(player);
    }

    public void leaveParty(Player player){
        partyManager.removePlayer(player);
        player.sendMessage(Component.text("You have left the party!", NamedTextColor.BLUE));
    }
}