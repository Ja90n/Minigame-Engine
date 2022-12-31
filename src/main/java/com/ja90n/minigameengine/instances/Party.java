package com.ja90n.minigameengine.instances;

import com.ja90n.minigameengine.MinigameEngine;
import com.ja90n.minigameengine.managers.PartyManager;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {
    private UUID partyLeader;
    private final PartyManager partyManager;
    private final MinigameEngine minigameEngine;
    private final ArrayList<UUID> players;
    private final ArrayList<UUID> invitedPlayers;

    public Party(Player leader, PartyManager partyManager, MinigameEngine minigameEngine){
        this.partyManager = partyManager;
        this.minigameEngine = minigameEngine;
        partyLeader = leader.getUniqueId();
        players = new ArrayList<>();
        invitedPlayers = new ArrayList<>();
        players.add(leader.getUniqueId());
    }

    public void joinGame(RegisteredServer server){
        for (UUID uuid : players){
            if (minigameEngine.getServer().getPlayer(uuid).isPresent()){
                Player player = (minigameEngine.getServer().getPlayer(uuid).get());
                player.createConnectionRequest(server).fireAndForget();
                player.sendMessage(Component.text("The party leader has joined the game!", NamedTextColor.BLUE));
            }
        }
    }

    public void leaveGame(RegisteredServer server){
        if (!minigameEngine.getServer().getServer("lobby").isPresent()) return;
        for (UUID uuid : players){
            if (minigameEngine.getServer().getPlayer(uuid).isPresent()){
                Player player = (minigameEngine.getServer().getPlayer(uuid).get());
                player.createConnectionRequest(minigameEngine.getServer().getServer("lobby").get()).fireAndForget();
                player.sendMessage(Component.text("The party leader has left the game!", NamedTextColor.BLUE));
            }
        }
    }

    public void addPlayer(Player player){
        players.add(player.getUniqueId());
        invitedPlayers.remove(player.getUniqueId());
    }

    public void removePlayer(Player player){
        players.remove(player.getUniqueId());
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public UUID getPartyLeader() {
        return partyLeader;
    }

    public ArrayList<UUID> getInvitedPlayers() {
        return invitedPlayers;
    }

    public void setPartyLeader(UUID partyLeader) {
        this.partyLeader = partyLeader;
    }
}
