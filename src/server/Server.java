package server;


import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    private ServerSocket socket;

    public Server(int port) throws IOException{
        socket = new ServerSocket(8080);
    }

    public void acceptConnections() throws IOException{
        while(!socket.isClosed()){
            Connection connection = new Connection(socket.accept());
            Thread thread = new Thread(connection);
            thread.start();
        }
    }

    public void close() throws IOException{
        socket.close();
    }
}
