package client;

import UsefulTools.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class ClientApplication {
    private BufferedReader reader;
    private Client myClient;
    private boolean running;
    private String myName;
    private long startTime;

    public ClientApplication(int port) {
        reader = new BufferedReader(new InputStreamReader(System.in));
        running = true;
        startTime = System.currentTimeMillis() / 60000;
        try {
            myClient = new Client(port);
        } catch (IOException e) {
            System.out.print("Failed to Connect. Exiting...");
            System.exit(0);
        }
        setName(Message.newMessageParse("/rename", myClient.ID));
    }

    public ClientApplication(InetAddress address, int port) {
        reader = new BufferedReader(new InputStreamReader(System.in));
        running = true;
        myName = "";
        startTime = System.currentTimeMillis() / 60000;
        try {
            myClient = new Client(address, port);
        } catch (IOException e) {
            System.out.print("Failed to Connect. Exiting...");
            System.exit(0);
        }
        setName(Message.newMessageParse("/name", myClient.ID));
    }

    public void start() {
        System.out.println("Connected!");
        Message output = new Message();
        Message input = new Message();
        while (running) {
            try {                                   //reading console
                if (reader.ready()) {
                    output = Message.newMessageParse(reader.readLine(), myClient.ID);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error reading message");
            }

            if (output.isSlashCommand()) { //checking if it's a command
                handleStringCommands(output);
                output = new Message();
            } else if (!output.isEmpty()) { //not a command, but not empty so send message
                try {
                    myClient.send(output.toCharArray());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Send Message Failed");
                }
                output = new Message();
            }
            try {
                if (myClient.ready()) { //if the client has data then grab and print
                    input = new Message(myClient.receive());
                    System.out.println("full: " + input);
                    System.out.println("message:" + input.getMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (input.isMessage()) {
                System.out.println(input.getMessage());
                input = new Message();
            }
        }
        System.out.println("Disconnected");
        try {
            myClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleStringCommands(Message data) {
        switch (data.getCommand()) {
            case "/help": {
                System.out.println("Commands are:");
                System.out.println("/quit to quit");
                System.out.println("/uptime to show current connected session time");
                System.out.println("/rename to start name change routine");
                System.out.println("/msg +username then message to private message");
                System.out.println("/online to see all online users");
                break;
            }
            case "/quit": {
                running = false;
                try {
                    myClient.send(data.toCharArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "/rename": {
                setName(data);
                break;
            }
            case "/uptime": {
                long currentTime = System.currentTimeMillis() / 60000;
                long sessionTime = currentTime - startTime;
                System.out.println("This session time length is: " + sessionTime + " minutes");
                break;
            }
            case "/online": {
                try {
                    myClient.send(data.toCharArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "/msg": {
                try {
                    myClient.send(data.toCharArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            default: {
                System.out.println("Invalid Command, type /help for more details");
            }
        }
    }

    private void setName(Message nameMessage) {
        System.out.println("Please input your handle:");
        try {
            while (!reader.ready()) ;
            myName = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            nameMessage.toSysCommand();
            nameMessage.setArguments(myName);
            myClient.send(nameMessage.toCharArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
