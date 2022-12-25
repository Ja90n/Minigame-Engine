package com.ja90n.minigameengine.enums;

public enum MessageType {

    SEND_PLAYER("sendPlayer", 2),
    KICK_PLAYER("kickPlayer", 1),
    KEEP_ALIVE("keepAlive", 0),
    PLAYERCOUNT("playerCount", 0),
    GAMESTATE("gameState", 0);

    private String message;
    private int args;

    MessageType(String message, int args) {
        this.message = message;
        this.args = args;
    }

    public int getArgs() {
        return args;
    }

    public String getMessage() {
        return message;
    }
}
