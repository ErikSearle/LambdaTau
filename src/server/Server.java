package server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        Socket socket = serverSocket.accept();
        OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream());
        output.write("Hi Mom");
        output.flush();
        output.close();
        serverSocket.close();
    }
}
