package ServerClient;

import sun.nio.cs.StandardCharsets;

import java.io.File;
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
    private FileWriter writer;
    private Scanner in;
    private File server_properties;
    private List<String> lines;


    public ServerProperties() throws IOException {
        server_properties = new File("Server Properties.txt");
        in = new Scanner(server_properties);
        createPropertiesFile();



    }

    private void createPropertiesFile() throws IOException {
        if(server_properties.createNewFile()){
            propertiesContents();
            Files.write(Paths.get("Server Properties.txt"), lines,StandardOpenOption.CREATE,StandardOpenOption.APPEND);
        } else{
            System.out.println("Loading properties file");
            in.useDelimiter("\\s*Server Name =\\s");
            System.out.println(in.delimiter());
            server_name = in.nextLine();
            System.out.println(server_name);
            in.useDelimiter("\\s*Port =\\s");
            System.out.println(in.delimiter());
            in.nextLine();
            port = in.nextInt();
            System.out.println(port);
            in.useDelimiter("\\s*Max users =\\s");
            System.out.println(in.delimiter());
            in.nextLine();
            max_users = in.nextInt();

            System.out.println("Properties loaded");
            System.out.println(server_name);
            System.out.println(port);
            System.out.println(max_users);
        }




        }

        private void propertiesContents(){
            lines = Arrays.asList("Server Name = LambdaTau","Port = 8080", "Max users = 10");
            server_name = "LambdaTau";
            port = 8080;
            max_users = 10;
        }

}