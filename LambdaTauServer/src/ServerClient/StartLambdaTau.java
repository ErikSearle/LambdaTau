package ServerClient;

import server.Server;

import java.io.IOException;

public class StartLambdaTau {
    public static void main(String[] args) throws IOException  {
    printWelcome();
    ServerProperties properties = new ServerProperties();
    properties.loadPropertiesFile();
    Server server_start = new Server(properties.getServer_name(),properties.getPort(),properties.getMax_users(),properties.getAdmin_port(),properties.getPassword());
    Thread running = new Thread(server_start);
    running.start();
    }

    public static void printWelcome() throws IOException {//print the welcome screen

        System.out.println("Welcome to:");
        System.out.println("\n" +
                "\n" +
                "  _                    _         _      _____           \n" +
                " | |    __ _ _ __ ___ | |__   __| | __ |_   _|_ _ _   _ \n" +
                " | |   / _` | '_ ` _ \\| '_ \\ / _` |/ _` || |/ _` | | | |\n" +
                " | |__| (_| | | | | | | |_) | (_| | (_| || | (_| | |_| |\n" +
                " |_____\\__,_|_| |_| |_|_.__/ \\__,_|\\__,_||_|\\__,_|\\__,_|\n" +
                "                                                        \n" +
                "\n");


    }

}