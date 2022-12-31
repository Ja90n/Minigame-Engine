package com.ja90n.minigameengine.managers;

import com.ja90n.minigameengine.MinigameEngine;
import com.ja90n.minigameengine.instances.Party;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyManager {

    private final MinigameEngine minigameEngine;
    private final List<Party> parties;
    private final List<UUID> players;

    public PartyManager(MinigameEngine minigameEngine){
        this.minigameEngine = minigameEngine;
        parties = new ArrayList<>();
        players = new ArrayList<>();
    }

    public void newParty(Player leader){
        Party party = new Party(leader,this,minigameEngine);
        parties.add(party);
        players.add(leader.getUniqueId());
    }

    public void addPlayer(Player player,String playerName){
        if (!players.contains(player.getUniqueId())){
            if (getParty(minigameEngine.getServer().getPlayer(playerName)) != null){
                getParty(minigameEngine.getServer().getPlayer(playerName)).addPlayer(player);
                players.add(player.getUniqueId());
                TextComponent textComponent = Component.text()
                        .content("You have been added to the party of")
                        .color(TextColor.color(85, 85, 255))
                        .append(Component.text(playerName, NamedTextColor.WHITE)).build()
                        .append(Component.text(" !",NamedTextColor.BLUE));
                player.sendMessage(textComponent);
            } else {
                TextComponent textComponent = Component.text("Party you tried to join does not exist!",NamedTextColor.RED);
                player.sendMessage(textComponent);
            }
        } else {
            TextComponent textComponent = Component.text("You are already in a party",NamedTextColor.RED);
            player.sendMessage(textComponent);
        }
    }

    public void invitePlayer(Player sender, Player receiver){
        TextComponent message = Component.text(sender.getUsername(), NamedTextColor.WHITE)
                .append(Component.text(" has invited you to their party!", NamedTextColor.BLUE));

        TextComponent clickable = Component.text("Click here to join their party!",NamedTextColor.LIGHT_PURPLE)
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,"/p join " + sender.getUsername()))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.text("Join the party of ",NamedTextColor.BLUE)
                        .append(Component.text(sender.getUsername()))));

        TextComponent finalMessage = message.append(clickable);

        receiver.sendMessage(finalMessage);
    }

    public void removePlayer(Player player){
        if (getParty(player) != null){
            Party party = getParty(player);
            party.removePlayer(player);
            if (party.getPlayers().isEmpty()){
                disbandParty(party);
            } else {
                TextComponent textComponent = Component.text(player.getUsername() +" has left the party!",NamedTextColor.RED);
                sendMessage(party,textComponent);
                if (party.getPartyLeader().equals(player.getUniqueId())){
                    changeLeader(player,party);
                }
            }
            players.remove(player.getUniqueId());
        } else {
            player.sendMessage(Component.text("You are not in a party!",NamedTextColor.RED));
        }
    }

    public void changeLeader(Player player, Party party){
        party.setPartyLeader(player.getUniqueId());
        TextComponent textComponent = Component.text(String.valueOf(minigameEngine.getServer().getPlayer(party.getPlayers().get(0))), NamedTextColor.WHITE)
                .append(Component.text(" has been promoted to party leader!",NamedTextColor.BLUE));
        sendMessage(party,textComponent);
    }

    public void disbandParty(Party party){
        List<UUID> list = new ArrayList<>();
        for (UUID uuid : party.getPlayers()){
            if (minigameEngine.getServer().getPlayer(uuid).isPresent()){
                minigameEngine.getServer().getPlayer(uuid).get().sendMessage(Component.text("Party was disbanded",NamedTextColor.RED));
                list.add(uuid);
            }
        }
        for (UUID uuid : list){
            removePlayer(minigameEngine.getServer().getPlayer(uuid).get());
        }
        party.getPlayers().clear();
        list.clear();
        parties.remove(party);
    }


    public void sendMessage(Party party, TextComponent textComponent){
        for (UUID uuid : party.getPlayers()){
            if (minigameEngine.getServer().getPlayer(uuid).isPresent()){
                minigameEngine.getServer().getPlayer(uuid).get().sendMessage(textComponent);
            }
        }
    }

    public void listParty(Player player){
        if (getParty(player) != null){
            Party party = getParty(player);
            player.sendMessage(Component.text("These are the players in your party: ",NamedTextColor.BLUE));
            if (minigameEngine.getServer().getPlayer(party.getPartyLeader()).isPresent()){
                player.sendMessage(Component.text("- ",NamedTextColor.BLUE)
                        .append(Component.text(minigameEngine.getServer().getPlayer(party.getPartyLeader()).get().getUsername()).color(NamedTextColor.GOLD)));
            }
            for (UUID uuid : party.getPlayers()){
                if (!uuid.equals(party.getPartyLeader())){
                    if (minigameEngine.getServer().getPlayer(uuid).isPresent()){
                        player.sendMessage(Component.text("- ",NamedTextColor.BLUE).append(Component.text(minigameEngine.getServer().getPlayer(uuid).get().getUsername(),NamedTextColor.WHITE)));
                    }
                }
            }
        } else {
            player.sendMessage(Component.text("You are not in a party!",NamedTextColor.RED));
        }
    }
    public Party getParty(Optional<Player> player){
        if (player.isPresent()){
            for (Party party : parties){
                if (party.getPlayers().contains(player.get().getUniqueId())){
                    return party;
                }
            }
        }
        return null;
    }

    public Party getParty(Player player){
        for (Party party : parties){
            if (party.getPlayers().contains(player.getUniqueId())){
                return party;
            }
        }
        return null;
    }

    public Party getParty(String name){
        Optional<Player> optionalTarget = minigameEngine.getServer().getPlayer(name);
        if (optionalTarget.isEmpty()){
            return null;
        }
        UUID targetUUID = optionalTarget.get().getUniqueId();
        for (Party party : parties){
            for (UUID playerUUID : party.getPlayers()){
                if (playerUUID == targetUUID){
                    return party;
                }
            }
        }
        return null;
    }

    public List<Party> getParties() {
        return parties;
    }

    public List<UUID> getPlayers() {
        return players;
    }
}
