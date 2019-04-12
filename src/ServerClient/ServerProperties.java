package ServerClient;

import sun.nio.cs.StandardCharsets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
    private Scanner in;
    private File server_properties;
    private List<String> lines;


    public ServerProperties() throws IOException {

        try {

            server_properties = new File("Server Properties.txt");
            in = new Scanner(server_properties);
            //System.out.println("Properties found");
        } catch (FileNotFoundException e) {
            System.out.println("Properties not found");
            server_properties = new File("Server Properties.txt");
            propertiesContents();
            server_properties.createNewFile();
            Files.write(Paths.get("Server Properties.txt"), lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            in = new Scanner(server_properties);
        }
    }

    public void loadPropertiesFile()  {
        try {
            if (server_properties.createNewFile()) {
                propertiesContents();
                Files.write(Paths.get("Server Properties.txt"), lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } else {
                System.out.println("Loading properties file..." + "\n");

                in.skip("Server Name =\\s*");
                server_name = in.nextLine();
                in.skip("Port =\\s*");
                port = in.nextInt();
                in.nextLine();
                in.skip("Max users =\\s*");
                max_users = in.nextInt();


            }
        }
        catch (IOException e){
            System.out.println("Error Loading Server Properties!");
            System.out.println("Defaulting server properties");

            server_name = "LamdaTau";
            port = 8080;
            max_users = 10;
        }
        finally {
            System.out.println("Properties loaded");
            System.out.println("Server Name : " + server_name);
            System.out.println("Bound to port Number : " + port);
            System.out.println("Maximum users : " + max_users);
        }




        }

        private void propertiesContents(){
            lines = Arrays.asList("Server Name = LambdaTau","Port = 8080", "Max users = 10");
            server_name = "LambdaTau";
            port = 8080;
            max_users = 10;
        }

}