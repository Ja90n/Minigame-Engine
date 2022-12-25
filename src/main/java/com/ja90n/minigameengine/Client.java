package com.ja90n.minigameengine;

import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private final MinigameEngine minigameEngine;
    private BufferedReader in;
    private PrintWriter out;
    private final ServerCommunicationHandler serverCommunicationHandler;
    private final Socket clientSocket;
    private String name;
    private RegisteredServer registeredServer;

    public Client(Socket clientSocket, MinigameEngine minigameEngine, ServerCommunicationHandler serverCommunicationHandler) throws IOException {
        this.minigameEngine = minigameEngine;
        this.serverCommunicationHandler = serverCommunicationHandler;
        this.clientSocket = clientSocket;

        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(),true);

        initialConnect();
    }

    public void initialConnect() {
        minigameEngine.getServer().getScheduler().buildTask(minigameEngine, () -> {
            while (true){
                try {
                    String request = in.readLine();
                    String[] args = request.split(":");

                    if (!args[1].equals("proxy")) { return; }
                    if (!args[2].equals("initialConnect")){ return; }

                    String serverName = args[0];
                    if (serverCommunicationHandler.getAvailableServers().isEmpty()){ return; }
                    for (RegisteredServer target : serverCommunicationHandler.getAvailableServers()){
                        if (serverName.equals(target.getServerInfo().getName())){
                            name = serverName;
                            registeredServer = target;
                            break;
                        }
                    }
                    if (registeredServer == null) { return; }

                    String random = args[3];
                    sendMessage("proxy:" + args[0] + ":initialConnect:" + random);

                    run();
                    break;
                } catch (IOException e) {}
            }
        });
    }

    public void run(){
        minigameEngine.getServer().getScheduler().buildTask(minigameEngine, () -> {
            while (true) {
                try {
                    String request = in.readLine();
                    serverCommunicationHandler.incomingMessage(request, this);
                } catch (IOException e) {
                    minigameEngine.getLogger().error("Server " + name + " has disconnected");
                    break;
                } finally {
                    try {
                        clientSocket.close();
                        in.close();
                    } catch (IOException ignored) {}
                }
            }
        }).schedule();
    }

    private void sendMessage(String message){
        out.println(message);
    }

    public String getName() {
        return name;
    }

    public RegisteredServer getRegisteredServer() {
        return registeredServer;
    }
}
