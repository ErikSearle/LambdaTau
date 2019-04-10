package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {

    private ServerSocket socket;
    static ArrayList<Connection> allConnections = new ArrayList<>();

    public Server(int port) throws IOException {
        socket = new ServerSocket(port);
    }

    static void sendAll(char[] message) throws IOException {
        for (Connection allConnection : allConnections) {
            if (allConnection != null) {
                allConnection.send(Arrays.copyOf(message, message.length));
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
