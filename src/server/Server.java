package server;


import UsefulTools.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {

    private ServerSocket socket;
    static ArrayList<Connection> allConnections = new ArrayList<>();
    static ArrayList<String> allNames = new ArrayList<>();

    public Server(int port) throws IOException {
        socket = new ServerSocket(port);
    }

    static void sendAll(Message message) throws IOException {
        int id = message.getSenderID();
        message.setSender(allNames.get(id));
        char[] data = message.getMessageChars(true);
        for (Connection allConnection : allConnections) {
            if (allConnection != null && allConnection.threadID != id) {
                allConnection.send(Arrays.copyOf(data, data.length));
            }
        }
    }

    public void acceptConnections() throws IOException {
        while (!socket.isClosed()) {
            Connection connection = new Connection(socket.accept());
            allConnections.add(connection);
            Thread thread = new Thread(connection);
            thread.start();
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}
