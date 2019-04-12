package server;


import UsefulTools.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Server {

    private volatile ArrayList<Connection> allConnections;
    private volatile ArrayList<String> allNames;
    private volatile PriorityQueue<Message> messageQueue = new PriorityQueue<>();
    private ServerSocketListener socket;
    private boolean online;
    private Thread socketThread;

    public Server(int port) throws IOException {
        allConnections = new ArrayList<>();
        allNames = new ArrayList<>();
        socket = new ServerSocketListener(port, this);
        socketThread = new Thread(socket);
    }

    public void start() {
        online = true;
        socketThread.start();
        System.out.println("Started");
        Message current;
        while (online) {
            if (!messageQueue.isEmpty()) {
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
        message.addPrefix(allNames.get(id) + ":");
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
        messageQueue.add(m);
    }

    public void addConnection(Connection c) {
        allConnections.add(c);
        allNames.add("default");

    }


    private void executeCommand(Message info) throws IOException {
        int senderID = info.getSenderID();
        switch (info.getCommand()) {
            case "name:":
                allNames.set(senderID, info.getArguments());
                Message message = Message.newMessageParse("Name is set to: " + info.getArguments(), Character.MAX_VALUE);
                send(message.toCharArray(), senderID);
                break;
            case "pmsg:":
                if (allNames.contains(info.getArguments())) {
                    int receiver = allNames.indexOf(info.getArguments());
                    String sender = allNames.get(senderID);
                    info = Message.newMessageParse(sender + " whispers: " + info.getMessage(), Character.MAX_VALUE);
                    send(info.toCharArray(), receiver);
                } else {
                    String error = info.getArguments() + " is offline or not found";
                    Message returnMsg = Message.newMessageParse(error, Character.MAX_VALUE);
                    send(returnMsg.toCharArray(), senderID);
                }
                break;
            case "online:": {
                String online = "Currently Online: " + allNames.toString();
                online = online.replace("[", "");
                online = online.replace("]", "");
                online = online.replaceAll("null,", "");
                online = online.replaceAll("\\s{2,}", " ");
                Message onlineMessage = Message.newMessageParse(online, Character.MAX_VALUE);
                send(onlineMessage.toCharArray(), senderID);
                break;
            }
            case "quit:":
                this.clientDisconnect(senderID);
        }
    }
}
