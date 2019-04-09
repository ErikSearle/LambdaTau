package client;

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
        setName();
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
        setName();
    }

    public void start() {
        System.out.println("Connected!");
        System.out.println("Welcome to the server " + myName);
        String readString = "";
        while (running) {
            try {                                   //reading console
                if (reader.ready()) {
                    readString = reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error reading message");
            }

            if (!readString.isEmpty() && readString.charAt(0) == '/') { //checking if it's a command
                handleStringCommands(readString);
                readString = "";
            } else if (!readString.isEmpty()) { //not a command, but not empty so send message
                String message = myName + ": " + readString;
                try {
                    myClient.send(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Send Message Failed");
                }
                readString = "";
            }
            try {
                if (myClient.ready()) { //if the client has data then grab and print
                    System.out.println(myClient.receive());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Disconnected");
        try {
            myClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleStringCommands(String s) {
        s = s.toLowerCase();
        String name = "";
        if (s.startsWith("/msg")) { //peeling the name off the command so the switch works
            name = s.substring(5);
            s = s.substring(0, 4);
        }
        switch (s) {
            case "/help": {
                System.out.println("Commands are:");
                System.out.println("/quit to quit");
                System.out.println("/uptime to show current connected session time");
                System.out.println("/rename to change current name");
                System.out.println("/msg +username to private message");
                System.out.println("/online to see all online users");
                break;
            }
            case "/quit": {
                running = false;
                break;
            }
            case "/rename": {
                setName();
                break;
            }
            case "/uptime": {
                long currentTime = System.currentTimeMillis() / 60000;
                long sessionTime = currentTime - startTime;
                System.out.println("This session time length is: " + sessionTime + " minutes");
                break;
            }
            case "/online": {
                System.out.println("not implemented yet");
                break;
            }
            case "/msg": {
                System.out.println("not implemented yet msg attempt to: " + name);
                break;
            }
            default: {
                System.out.println("Invalid Command, type /help for more details");
            }
        }
    }

    private void setName() {
        System.out.println("Please input your handle:");
        try {
            while (!reader.ready()) ;
            myName = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
