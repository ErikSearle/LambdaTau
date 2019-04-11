package server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerSocketListener implements Runnable {
    private ServerSocket socket;
    Server server;

    public ServerSocketListener(int port, Server s) throws IOException {
        socket = new ServerSocket(port);
        server = s;
    }

    private void acceptConnections() throws IOException {
        while (!socket.isClosed()) {
            Connection connection = new Connection(socket.accept(), server);
            server.addConnection(connection);
            System.out.println("added");
            Thread thread = new Thread(connection);
            thread.start();
        }
    }

    @Override
    public void run() {
        try {
            acceptConnections();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}
