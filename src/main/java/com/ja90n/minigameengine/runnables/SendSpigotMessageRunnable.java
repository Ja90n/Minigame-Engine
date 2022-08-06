package com.ja90n.minigameengine.runnables;

import com.ja90n.minigameengine.MinigameEngine;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.text.TextComponent;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

public class SendSpigotMessageRunnable {

    private ScheduledTask scheduledTask;

    public SendSpigotMessageRunnable(MinigameEngine minigameEngine, String key, String value, Jedis jedis, Player player,TextComponent textComponent){
        scheduledTask = minigameEngine.getServer().getScheduler()
                .buildTask(minigameEngine, () -> {
                    if (jedis.get(key).equals("")){
                        jedis.set(key,value);
                        player.sendMessage(textComponent);
                        scheduledTask.cancel();
                    }
                })
                .repeat(200L, TimeUnit.MILLISECONDS)
                .schedule();
    }
}
