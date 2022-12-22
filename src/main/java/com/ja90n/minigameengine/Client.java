package com.ja90n.minigameengine;

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

    public Client(Socket clientSocket, MinigameEngine minigameEngine, ServerCommunicationHandler serverCommunicationHandler) throws IOException {
        this.minigameEngine = minigameEngine;
        this.serverCommunicationHandler = serverCommunicationHandler;
        this.clientSocket = clientSocket;
        name = "";

        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(),true);

        run();
    }

    public void run(){
        minigameEngine.getServer().getScheduler().buildTask(minigameEngine, () -> {
                    while (true) {
                        try {
                            String request = in.readLine();
                            String[] strings = request.split(":");
                            if (name.equals("")){
                                name = strings[1];
                            }
                            if (name.equals(strings[1])){
                                serverCommunicationHandler.incomingMessage(request);
                            }
                        } catch (IOException e) {
                            minigameEngine.getLogger().error("IOException l38 in Client");
                        } finally {
                            try {
                                clientSocket.close();
                                in.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                }).schedule();
    }
}
