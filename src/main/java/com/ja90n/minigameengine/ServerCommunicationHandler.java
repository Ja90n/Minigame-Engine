package com.ja90n.minigameengine;

import com.velocitypowered.api.scheduler.ScheduledTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerCommunicationHandler {

    private final ScheduledTask scheduledTask;
    private final int PORT = 9090;
    private ArrayList<Client> clients;

    public ServerCommunicationHandler(MinigameEngine minigameEngine) {
        scheduledTask = minigameEngine.getServer().getScheduler().buildTask(minigameEngine, () -> {

            clients = new ArrayList<>();

            try {
                ServerSocket listener = new ServerSocket(PORT);
                while (true){

                    Socket clientSocket = listener.accept();
                    Client client = new Client(clientSocket,minigameEngine,this);
                    clients.add(client);

                    minigameEngine.getLogger().info("Found a client!");

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }).schedule();
    }

    public void incomingMessage(String message){

    }
}
