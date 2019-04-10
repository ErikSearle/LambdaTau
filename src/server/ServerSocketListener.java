package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ServerSocketListener implements Runnable {
    private ServerSocket socket;
    private ArrayList<Connection> allConnections;
    private ArrayList<String> allNames;

    public ServerSocketListener(int port) throws IOException {
        socket = new ServerSocket(port);
    }

    private void acceptConnections() throws IOException {
        while (!socket.isClosed()) {
            Connection connection = new Connection(socket.accept());
            allConnections.add(connection);
            allNames.add("default");
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

    public void setConnectionList(ArrayList<Connection> arraylist) {
        allConnections = arraylist;
    }

    public void setNameList(ArrayList<String> arraylist2) {
        allNames = arraylist2;
    }
}
