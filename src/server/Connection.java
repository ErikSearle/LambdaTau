package server;

import security.Checksum;
import security.Encryptor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Connection implements Runnable {

    private static ArrayList<Connection> allConnections = new ArrayList<>();
    private static int threadIDCounter = 0;
    private Socket socket;
    private InputStreamReader input;
    private OutputStreamWriter output;
    private Encryptor encryptor;
    private int threadID;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        input = new InputStreamReader(this.socket.getInputStream());
        output = new OutputStreamWriter(this.socket.getOutputStream());
        encryptor = Encryptor.negotiateKeysServerSide(input, output);
        this.threadID = threadIDCounter++;
        allConnections.add(this);
    }

    @Override
    public void run() {
        boolean socketOpen = true;
        try {
            while (socketOpen) {
                char[] decryptedMessage = receive();
                if (decryptedMessage != null) {
                    int checksum = Checksum.calculateCheckSum(decryptedMessage);
                    send(decryptedMessage);
                    if (decryptedMessage.length == 0 && decryptedMessage[0] == 26) {
                        socketOpen = false;
                    }
                }
            }
            close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Encrypts message before sending across open connection
     *
     * @param message Message to send
     * @throws IOException Unable to send message
     */
    private void send(char[] message) throws IOException {
        for (Connection allConnection : allConnections) {
            if (allConnection != null) {
                allConnection.output.write(encryptor.encrypt(message), 0, message.length);
                allConnection.output.flush();
            }
        }
    }

    private void send(int i) throws IOException {
        char[] sendable = new char[1];
        sendable[0] = (char) i;
        send(sendable);
    }

    /**
     * Receives, decrypts, and returns the next message from the open socket up to a size of 1000 bytes
     *
     * @return The decrypted message
     * @throws IOException Unable to receive message
     */
    private char[] receive() throws IOException {
        char[] buffer = new char[1000];
        int charsRead = input.read(buffer, 0, buffer.length);
        if (charsRead > 0) {
            return encryptor.decrypt(Arrays.copyOfRange(buffer, 0, charsRead));
        } else return null;
    }

    private void close() throws IOException {
        input.close();
        output.close();
        socket.close();
        allConnections.set(threadID, null);
    }
}
