package client;

import security.Encryptor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class Client{

    private Socket socket;
    private InputStreamReader input;
    private OutputStreamWriter output;
    private Encryptor encryptor;

    /**
     * Constructor used to connect to a localhost port. Exceptions thrown because it is better to handle these
     * exceptions at the application layer.
     * @param port The port to connect to
     * @throws IOException Unable to connect to desired port
     */
    public Client(int port) throws IOException{
        socket = new Socket(InetAddress.getLocalHost(), port);
        input = new InputStreamReader(socket.getInputStream());
        output = new OutputStreamWriter(socket.getOutputStream());
        encryptor = Encryptor.negotiateKeys(input, output);
    }

    /**
     * Constructor which allows the user to connect to an external address and port. Exceptions thrown because it is
     * better to handle these at the application layer.
     * @param address Internet address to point to
     * @param port port to connect to
     * @throws IOException Unable to make connection with desired address and port
     */
    public Client(InetAddress address, int port) throws IOException{
        socket = new Socket(address, port);
        input = new InputStreamReader(socket.getInputStream());
        output = new OutputStreamWriter(socket.getOutputStream());
    }

    /**
     * Encrypts message before sending across open connection
     * @param message Message to send
     * @throws IOException Unable to send message
     */
    public void send(String message) throws IOException{
        send(message.toCharArray());
    }

    /**
     * Encrypts message before sending across open connection
     * @param message Message to send
     * @throws IOException Unable to send message
     */
    public void send(char[] message) throws IOException{
        output.write(encryptor.encrypt(message), 0, message.length);
    }

    /**
     * Receives, decrypts, and returns the next message from the open socket up to a size of 1000 bytes
     * @return The decrypted message
     * @throws IOException Unable to receive message
     */
    public String receive() throws IOException{
        char[] buffer = new char[1000];
        int charsRead = input.read(buffer, 0, buffer.length);
        char[] decryptedMessage = encryptor.decrypt(Arrays.copyOfRange(buffer, 0, charsRead));
        return String.copyValueOf(decryptedMessage);
    }

    /**
     * Closes the connection with the socket, as well as socket reader and writer
     * @throws IOException Unable to close
     */
    public void close() throws IOException{
        input.close();
        output.close();
        socket.close();
    }
}
