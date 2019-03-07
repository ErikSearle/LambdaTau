package client;

import java.io.IOException;

public class TestMain {

    public static void main(String[] args) throws IOException {
        Client client = new Client(8080);
        for(int i=0; i<100001; i++) {
            client.send("Hi Mom");
            String checksum = client.receive();
            if(i%10000 == 0) {
                System.out.println((int) checksum.charAt(0));
            }
        }
        client.close();
    }
}
