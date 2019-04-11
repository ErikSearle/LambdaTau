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
        socket = new ServerSocketListener(port, this); //todo this doesnt work
        socketThread = new Thread(socket);
    }

    public void start() {
        online = true;
        socketThread.start();
        System.out.println("started");
        Message current;
        while (online) {
            if (!messageQueue.isEmpty()) { //todo figure out why this won't grab messages
                System.out.println("grabbed message");
                current = messageQueue.poll();
                if (current != null && current.isSystemCommand()) {
                    try {
                        executeCommand(current);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (current != null && current.isMessage()) {
                    try {
                        sendAll(current);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    void sendAll(Message message) throws IOException {
        int id = message.getSenderID();
        message.addPrefix(allNames.get(id));
        char[] messageSet = message.toCharArray();
        for (Connection allConnection : allConnections) {
            if (allConnection != null && allConnection.threadID != id) {
                allConnection.send(Arrays.copyOf(messageSet, messageSet.length));
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
        System.out.println("added to queue");
        messageQueue.add(m);
        System.out.println(messageQueue.size());
        System.out.println(messageQueue.isEmpty());
    }

    public void addConnection(Connection c) {
        allConnections.add(c);
        allNames.add("default");
        System.out.println("connection:" + allConnections.size());

    }


    //TODO fix this entirely
    private void executeCommand(Message info) throws IOException {
        int senderID = info.getSenderID();
        switch (info.getCommand()) {
            case "name:":
                allNames.set(senderID, info.getArguments());
                String message = "Name is set to: " + info.getArguments();
                send(message.toCharArray(), senderID);
                break;
            case "pmsg:":
                if (allNames.contains(info.getArguments())) {
                    int receiver = allNames.indexOf(info.getArguments());
                    String sender = allNames.get(senderID);
                    String toSend = sender + " whispers:" + info.getMessage();
                    send(toSend.toCharArray(), receiver);
                } else {
                    String error = info.getArguments() + " is offline or not found";
                    send(error.toCharArray(), senderID);
                }
                break;
            case "online:": {
                String online = allNames.toString();
                online = online.replace("[", "");
                online = online.replace("]", "");
                send(online.toCharArray(), senderID);
                break;
            }
            case "quit:":
                this.clientDisconnect(senderID);
        }
    }
}
