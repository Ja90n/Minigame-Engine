package com.ja90n.minigameengine;

import com.ja90n.minigameengine.enums.MessageType;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ServerCommunicationHandler {

    private final ScheduledTask scheduledTask;
    private final int PORT = 9090;
    private ArrayList<Client> clients;
    private final MinigameEngine minigameEngine;
    private Collection<RegisteredServer> availableServers;

    public ServerCommunicationHandler(MinigameEngine minigameEngine) {
        this.minigameEngine = minigameEngine;

        availableServers = minigameEngine.getServer().getAllServers();

        minigameEngine.getLogger().info("Started looking for clients");

        scheduledTask = minigameEngine.getServer().getScheduler().buildTask(minigameEngine, () -> {
            clients = new ArrayList<>();
            try {
                ServerSocket listener = new ServerSocket(PORT);
                while (true){
                    Socket clientSocket = listener.accept();
                    Client client = new Client(clientSocket,minigameEngine,this);
                    clients.add(client);

                }
            } catch (IOException e) {
                minigameEngine.getLogger().error("huh");
            }
        }).schedule();
    }

    /*
    To receive a message from a client I use this layout:
    from server : to server : command : (args)

    for example:
    lobby:proxy:sendPlayer:(Player UUID):destinationServer
     */

    public void incomingMessage(String message, Client client){
        String[] args = message.split(":");

        System.out.println(args);

        if (!args[1].equals("proxy")) { return; }

        MessageType type = null;
        for (MessageType messageType : MessageType.values()){
            if (messageType.getMessage().equals(args[2])){
                type = messageType;
                break;
            }
        }
        if (type == null){ return; }

        if (type.equals(MessageType.SEND_PLAYER)){
            Optional<Player> optionalPlayer = minigameEngine.getServer().getPlayer(args[3]);
            if (!optionalPlayer.isPresent()){ return; }
            Player player = optionalPlayer.get();
            RegisteredServer registeredServer = getServer(args[4]);
            if (registeredServer == null) return;
            player.createConnectionRequest(registeredServer).fireAndForget();
            return;
        }

        if (type.equals(MessageType.KICK_PLAYER)){
            Optional<Player> optionalPlayer = minigameEngine.getServer().getPlayer(args[3]);
            if (!optionalPlayer.isPresent()){ return; }
            Player player = optionalPlayer.get();
            if (!minigameEngine.getServer().getServer("lobby").isPresent()){ return; }
            RegisteredServer registeredServer = minigameEngine.getServer().getServer("lobby").get();
            player.createConnectionRequest(registeredServer).fireAndForget();
            return;
        }
    }

    public Collection<RegisteredServer> getAvailableServers() {
        return availableServers;
    }

    public RegisteredServer getServer(String name){
        for (RegisteredServer registeredServer : minigameEngine.getServer().getAllServers()){
            if (registeredServer.getServerInfo().getName().equals(name)){
                return registeredServer;
            }
        }
        return null;
    }
}
