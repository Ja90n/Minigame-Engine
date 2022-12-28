/*
package com.ja90n.minigameengine.events;

import com.ja90n.minigameengine.MinigameEngine;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;

public class PlayerDisconnect {

    private MinigameEngine minigameEngine;

    public PlayerDisconnect(MinigameEngine minigameEngine){
        this.minigameEngine = minigameEngine;
    }

    @Subscribe(order = PostOrder.NORMAL)
    public void onDisconnect(DisconnectEvent event){
        if (minigameEngine.getPartyManager().getParty(event.getPlayer()) != null) {
            minigameEngine.getPartyManager().removePlayer(event.getPlayer());
        }
    }
}

 */
