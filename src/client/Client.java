package client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException {
        Socket socket = new Socket(InetAddress.getLocalHost(),8080);
        InputStreamReader input = new InputStreamReader(socket.getInputStream());
        char[] buffer = new char[100];
        System.out.println(input.read(buffer, 0, 100));
        System.out.println(String.copyValueOf(buffer));
        input.close();
        socket.close();
    }
}
