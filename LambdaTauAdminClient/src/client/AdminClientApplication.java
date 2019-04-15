package client;

import UsefulTools.Message.*;
import UsefulTools.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class AdminClientApplication {
    private BufferedReader reader;
    private AdminClient myClient;
    private boolean running;
    private String myName;
    private long startTime;
    private String password;

    public AdminClientApplication(int port, String password) {
        this.password = password;
        reader = new BufferedReader(new InputStreamReader(System.in));
        running = true;
        startTime = System.currentTimeMillis() / 60000;
        try {
            myClient = new AdminClient(port,password);
        } catch (IOException e) {
            System.out.print("Failed to Connect. Exiting...");
            System.exit(0);
        }
        setName(Message.newMessageParse("/rename", myClient.ID));
    }

    public AdminClientApplication(InetAddress address, int port, String password) {
        reader = new BufferedReader(new InputStreamReader(System.in));
        running = true;
        myName = "";
        startTime = System.currentTimeMillis() / 60000;
        try {
            myClient = new AdminClient(address, port, password);
        } catch (IOException e) {
            System.out.print("Failed to Connect. Exiting...");
            System.exit(0);
        }
        setName(Message.newMessageParse("/name", myClient.ID));
    }

    public void start() {
        Message output = new AdminMessage();
        Message input = new Message.AdminMessage();
        while (running) {
            try {                                   //reading console
                if (reader.ready()) {
                    String temp = reader.readLine();
                    if (!temp.isEmpty()) {
                        output = Message.AdminMessage.newMessageParse(temp, myClient.ID, myClient.password);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error reading message");
            }

            }  if (!output.isEmpty()) { //not a command, but not empty so send message
                try {
                    myClient.send(output.toCharArray() + password);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Send Message Failed");
                }
                output = new Message.AdminMessage();
            }
            try {
                if (myClient.ready()) { //if the client has data then grab and print
                    input = new Message.AdminMessage(myClient.receive());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (input.isMessage()) {
                System.out.println(input.getMessage());
                input = new Message.AdminMessage();
            }
            if (input.isSlashCommand()) { //checking if it's a command
                handleStringCommands(input);
                input = new Message.AdminMessage();
            }
        }
        System.out.println("Disconnected");
//        try {
//            myClient.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
                System.out.println("/shutdown will kick everyone from the server and close the server terminal");
                System.out.println("/kick [username] will kick the selected user from the server");
                break;
            }
            case "/quit": {
                running = false;
                try {
                    data.toSysCommand();
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
                    data.toSysCommand();
                    myClient.send(data.toCharArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "/msg": {
                try {
                    data.toSysCommand();
                    myClient.send(data.toCharArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "/shutdown": {
                try {
                    data.toSysCommand();
                    myClient.send(data.toCharArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "/kick" : {
                try {
                    data.toSysCommand();
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
            nameMessage.setArguments(myName);
            nameMessage.toSysCommand();
            myClient.send(nameMessage.toCharArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}