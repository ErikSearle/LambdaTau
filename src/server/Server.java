package server;


import UsefulTools.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Server {

    private ArrayList<Connection> allConnections;
    private ArrayList<String> allNames;
    private PriorityQueue<Message> messageQueue = new PriorityQueue<>();
    private ServerSocketListener socket;
    private boolean online;
    private Thread socketThread;

    public Server(int port) throws IOException {
        allConnections = new ArrayList<>();
        allNames = new ArrayList<>();
        socket = new ServerSocketListener(port, this); //todo write better object communication
        socketThread = new Thread(socket);
    }

    public void start() {
        online = true;
        socketThread.start();
        System.out.println("started");
        while (online) {
            ;
            if (allNames.size() > 1) {
                System.out.println(allNames);
            }
            if (messageQueue.size() > 0) {
                System.out.println("thick");
            }
        }
    }

    /*
                        if (message.isPrivCommand()) {
                        try {
                            parseInfo(message);
                        } catch (IOException e) {
                            System.out.println("failed to parse message");
                        }
                    } else {
                        sendAll(message);
                    }
     */

    void sendAll(Message message) throws IOException {
        int id = message.getSenderID();
        message.setSender(allNames.get(id));
        char[] data = message.getMessageChars(true);
        for (Connection allConnection : allConnections) {
            if (allConnection != null && allConnection.threadID != id) {
                allConnection.send(Arrays.copyOf(data, data.length));
            }
        }
    }

    private void send(char[] message, int threadNum) throws IOException {
        Connection target = allConnections.get(threadNum);
        target.send(message);
    }

    public void clientDisconnect(int threadID) {
        allConnections.set(threadID, null);
        allNames.set(threadID, null);
    }

    public void addToQueue(Message m) {
        messageQueue.add(m);
    }

    public void addConnection(Connection c) {
        allConnections.add(c);
        allNames.add("default");

    }


    //TODO fix this entirely
    /*private void parseInfo(Message info) throws IOException { // **&**!^&@ is the magic startframe lmao
        String command = info.getPrivCommandType();
        switch (command) {
            case "name:":
                allNames.set(threadID, info.getReceiver());
                String message = "Name is set to: " + info.getReceiver();
                send(message.toCharArray(), info.getSenderID());
                break;
            case "pmsg:":
                int senderID = info.getSenderID();
                if (allNames.contains(info.getReceiver())) {
                    int receiver = allNames.indexOf(info.getReceiver());
                    String sender = allNames.get(senderID);
                    String toSend = sender + " whispers:" + info.getMessage();
                    send(toSend.toCharArray(), receiver);
                } else {
                    String error = info.getReceiver() + " is offline or not found";
                    send(error.toCharArray(), senderID);
                }
                break;
            case "online:": {
                String online = allNames.toString();
                online = online.replace("[", "");
                online = online.replace("]", "");
                send(online.toCharArray(), info.getSenderID());
                break;
            }
            case "quit:":
                this.close();
        }
    }*/
}
