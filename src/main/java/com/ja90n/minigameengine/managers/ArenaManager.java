package com.ja90n.minigameengine.managers;

import com.ja90n.minigameengine.MinigameEngine;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager {
    private List<RegisteredServer> arenas = new ArrayList<>();

    public ArenaManager (MinigameEngine minigameEngine){
        for (RegisteredServer server : minigameEngine.getServer().getAllServers()){
            if (server.getServerInfo().getName().startsWith("arena0")){
                arenas.add(server);
            }
        }
    }

    public List<RegisteredServer> getArenas() { return arenas; }

    public RegisteredServer getArena(Player player){
        for (RegisteredServer server : arenas){
            for (Player player1 : server.getPlayersConnected()) {
                if (player1.getUniqueId().equals(player.getUniqueId())){
                    return server;
                }
            }
        }
        return null;
    }

    public RegisteredServer getArena(String name){
        for (RegisteredServer server : arenas){
            if (server.getServerInfo().getName().equals(name)){
                return server;
            }
        }
        return null;
    }

    public void removePlayer(Player player){

    }
}
