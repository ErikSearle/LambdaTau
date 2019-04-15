package ServerClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ServerProperties {
    private String server_name;
    private int port;
    private int max_users;
    private String password;
    private int admin_port;
    private Scanner in;
    private File server_properties;
    private List<String> lines;
    public ServerProperties() throws IOException {

        try {

            server_properties = new File("Server Properties.txt");
            in = new Scanner(server_properties);
        } catch (FileNotFoundException e) {
            System.out.println("Properties not found");
            server_properties = new File("Server Properties.txt");
            propertiesContents();
            server_properties.createNewFile();
            Files.write(Paths.get("Server Properties.txt"), lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            in = new Scanner(server_properties);
        }
    }

    public int getAdmin_port() {
        return admin_port;
    }

    public String getPassword() {
        return password;
    }

    public String getServer_name() {
        return server_name;
    }

    public int getPort() {
        return port;
    }

    public int getMax_users() {
        return max_users;
    }

    public void loadPropertiesFile() {
        try {
            if (server_properties.createNewFile()) {
                propertiesContents();
                Files.write(Paths.get("Server Properties.txt"), lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } else {
                System.out.println("Loading properties file..." + "\n");

                in.skip("Server Name =\\s*");//grabs the name
                server_name = in.nextLine();
                in.skip("Port =\\s*");//grabs the port number
                port = in.nextInt();
                in.nextLine();
                in.skip("Max users =\\s*");//grabs the max number of users from the file
                max_users = in.nextInt();
                in.nextLine();
                in.skip("Admin Port =\\s*");
                admin_port = in.nextInt();
                in.nextLine();
                in.skip("Admin Password =\\s*");
                password = in.next();


            }
        } catch (IOException e) {
            System.out.println("Error Loading Server Properties!");
            System.out.println("Defaulting server properties");

            server_name = "LamdaTau";
            port = 8080;
            max_users = 10;
            admin_port = 8081;
            password = "1111";
        } finally {
            System.out.println("Properties loaded");
            System.out.println("Server Name : " + server_name);
            System.out.println("Bound to port Number : " + port);
            System.out.println("Maximum users : " + max_users);
            System.out.println("Admin Server Port : " + admin_port);
            System.out.println("Admin password : " + password);
        }


    }

    private void propertiesContents() {//defaults the server properties
        lines = Arrays.asList("Server Name = LambdaTau", "Port = 8080", "Max users = 10", "Admin Port = 8081", "Admin Password = 1111");
        server_name = "LambdaTau";
        port = 8080;
        max_users = 10;
        admin_port = 8081;
        password = "1111";
    }

}