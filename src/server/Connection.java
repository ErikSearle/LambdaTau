package server;

import UsefulTools.Message;
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
    private static ArrayList<String> allNames = new ArrayList<>();
    private static int threadIDCounter = 0;
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
        this.threadID = threadIDCounter++;
        allConnections.add(this);
        allNames.add("default");
        send(threadID);
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

    @Override
    public void run() {
        boolean socketOpen = true;
        try {
            while (socketOpen) {
                char[] decryptedMessage = receive();
                if (decryptedMessage != null) {
                    int checksum = Checksum.calculateCheckSum(decryptedMessage);
                    Message message = new Message(decryptedMessage);
                    if (message.isPrivCommand()) {
                        try {
                            parseCommand(message);
                        } catch (IOException e) {
                            System.out.println("failed to parse message");
                        }
                    } else {
                        sendAll(message);
                    }
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
        output.write(encryptor.encrypt(message), 0, message.length);
        output.flush();
    }

    private void send(int i) throws IOException {
        char[] sendable = new char[1];
        sendable[0] = (char) i;
        send(sendable);
    }

    private void send(char[] message, int threadNum) throws IOException {
        Connection target = allConnections.get(threadNum);
        target.send(message);
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
        allNames.set(threadID, null);
    }

    private void parseCommand(Message info) throws IOException { // **&**!^&@ is the magic startframe lmao
        String command = info.getPrivCommandType();
        switch (command) {
            case "name:":
                allNames.set(threadID, info.getReceiver());
                String message = "Name is set to: " + info.getReceiver();
                send(message.toCharArray(), info.getSenderID());
                break;
            case "pmsg:":
                int senderID = info.getSenderID();
                if (allNames.contains(info.getReceiver())) {
                    int receiver = allNames.indexOf(info.getReceiver());
                    String sender = allNames.get(senderID);
                    String toSend = sender + " whispers:" + info.getMessage();
                    send(toSend.toCharArray(), receiver);
                } else {
                    String error = info.getReceiver() + " is offline or not found";
                    send(error.toCharArray(), senderID);
                }
                break;
            case "online:":
                String online = allNames.toString();
                online = online.replace("[", "");
                online = online.replace("]", "");
                send(online.toCharArray(), info.getSenderID());
                break;
        }
    }
}
