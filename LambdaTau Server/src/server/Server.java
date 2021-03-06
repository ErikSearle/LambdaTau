package server;


import UsefulTools.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Server implements Runnable{
    private String welcome = "\n Welcome to:   "  +
            "\n" +
            "   _                    _         _      _____           \n" +
            "  | |    __ _ _ __ ___ | |__   __| | __ |_   _|_ _ _   _ \n" +
            "  | |   / _` | '_ ` _ \\| '_ \\ / _` |/ _` || |/ _` | | | |\n" +
            "  | |__| (_| | | | | | | |_) | (_| | (_| || | (_| | |_| |\n" +
            "  |_____\\__,_|_| |_| |_|_.__/ \\__,_|\\__,_||_|\\__,_|\\__,_|\n" +
            "                                                        \n" +
            "\n" +
            "You are now entering : " + "*";
    private volatile ArrayList<Connection> allConnections;
    private volatile ArrayList<String> allNames;
    private volatile PriorityQueue<Message> messageQueue = new PriorityQueue<>();
    private String server_name;
    final int max_users;
    private ServerSocketListener socket;
    private boolean online;
    private Thread socketThread;


    public Server(String server_name, int port, int max_users) throws IOException {
        this.server_name = server_name;
        welcome = welcome.replace("*", server_name);
        this.max_users = max_users;
        allConnections = new ArrayList<>();
        allNames = new ArrayList<>();
        socket = new ServerSocketListener(port, this);
        socketThread = new Thread(socket);

    }

    public void start() {
        online = true;
        socketThread.start();
        System.out.println("Server Running");
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
        if (allNames.size() > max_users) {
           // Message too_full = Message.newMessageParse("You are not welcome", Character.MAX_VALUE); TODO figure out how to ge this to send properly
            String quitString = "2 " + 65535 + " /quit";
            Message quit_command = new Message(quitString);
            try {
                //send(too_full.toCharArray(), allConnections.size() - 1);TODO see above
                send(quit_command.toCharArray(), allConnections.size() - 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.clientDisconnect(allConnections.size() - 1);
        }
        else{
            Message server_welcome = Message.newMessageParse(welcome, Character.MAX_VALUE);
            try {
                send(server_welcome.toCharArray(), allConnections.size() -1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void executeCommand(Message info) throws IOException {
        int senderID = info.getSenderID();
        switch (info.getCommand()) {
            case "name:":
                allNames.set(senderID, info.getArguments());
                Message message = Message.newMessageParse("\n" + "Name is set to: " + info.getArguments(), Character.MAX_VALUE);
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


    @Override
    public void run() {
        start();
    }
}
