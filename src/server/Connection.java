package server;

import UsefulTools.Message;
import security.Checksum;
import security.Encryptor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Connection implements Runnable {


    private static int threadIDCounter = 0;
    private static PriorityQueue queue;
    final int threadID;
    private Socket socket;
    private InputStreamReader input;
    private OutputStreamWriter output;
    private Encryptor encryptor;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        input = new InputStreamReader(this.socket.getInputStream());
        output = new OutputStreamWriter(this.socket.getOutputStream());
        encryptor = Encryptor.negotiateKeysServerSide(input, output);
        queue = new PriorityQueue();
        this.threadID = threadIDCounter++;
        send(threadID); //informs client of it's ID
    }

    static PriorityQueue getQueue() {
        return queue;
    }

    @Override
    public void run() {
        boolean socketOpen = true;
        try {
            while (socketOpen) {
                char[] decryptedMessage = receive();
                if (decryptedMessage != null) {
                    int checksum = Checksum.calculateCheckSum(decryptedMessage);
                    Message message = new Message(decryptedMessage);
                    queue.add(message);
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
    void send(char[] message) throws IOException {
        output.write(encryptor.encrypt(message), 0, message.length);
        output.flush();
    }

    void send(int i) throws IOException {
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
        //input.close();
        // output.close();  //turns out these close the socket for every thread.
        //socket.close();
//TODO need to remove somehow
    }


}
