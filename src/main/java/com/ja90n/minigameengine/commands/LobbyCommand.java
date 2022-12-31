package com.ja90n.minigameengine.commands;

import com.ja90n.minigameengine.MinigameEngine;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.List;
import java.util.Optional;

public class LobbyCommand implements SimpleCommand {

    private MinigameEngine minigameEngine;

    public LobbyCommand(MinigameEngine minigameEngine) {
        this.minigameEngine = minigameEngine;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.source() instanceof Player){
            Player player = (Player) invocation.source();
            if (minigameEngine.getServer().getServer("lobby").isPresent()){
                RegisteredServer registeredServer = minigameEngine.getServer().getServer("lobby").get();
                player.createConnectionRequest(registeredServer).fireAndForget();
            }
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }
}
