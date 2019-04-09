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
        allNames.add("blank");
        send(threadID);
    }

    static void sendAll(char[] message) throws IOException {
        String manipulate = new String(message);
        int pos = manipulate.indexOf(" ");
        int id;
        String remove = manipulate.substring(0, pos);
        id = Integer.parseInt(remove);
        manipulate = manipulate.replace(id + "", allNames.get(id) + ":");
        message = manipulate.toCharArray();
        for (Connection allConnection : allConnections) {
            if (allConnection != null && allConnection.threadID != id) {
                allConnection.send(Arrays.copyOf(message, message.length));
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
                    if (new String(decryptedMessage).contains("**&**!^&@")) {
                        try {
                            parseInfo(decryptedMessage);
                        } catch (IOException e) {

                        }
                    } else {
                        sendAll(decryptedMessage);
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

    private void parseInfo(char[] msg) throws IOException {
        String raw = new String(msg);           // **&**!^&@ is the magic startframe lmao
        int id;
        int pos = raw.indexOf(" ");
        String value = raw.substring(0, pos);
        id = Integer.parseInt(value);
        raw = raw.replace(value + " ", "");
        pos = raw.indexOf(":");
        String info = "";
        String remove = raw.substring(0, pos + 1);
        info = raw.replace(remove, "");
        raw = raw.replace(info, "");
        switch (raw) {
            case "**&**!^&@name:":
                info = info.replace(" ", "_");
                allNames.set(threadID, info);
                String message = "Name is set to: " + info;
                send(message.toCharArray(), id);
                break;
            case "**&**!^&@pmsg:":
                int space = info.indexOf(" ");
                String data = "";
                data = info.substring(space);
                String name = info.substring(0, space);
                if (allNames.contains(name)) {
                    int receiver = allNames.indexOf(name);
                    String sender = allNames.get(id);
                    String toSend = sender + " whispers: " + data;
                    send(toSend.toCharArray(), receiver);
                } else {
                    String error = name + " is offline or not found";
                    send(error.toCharArray(), id);
                }
                break;
            case "**&**!^&@online:":
                String online = allNames.toString();
                online = online.replace("[", "");
                online = online.replace("]", "");
                send(online.toCharArray(), id);
                break;
        }
    }
}
