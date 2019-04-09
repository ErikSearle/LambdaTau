package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ClientApplication client;
        switch (args.length) {
            case 1: {
                try {
                    client = new ClientApplication(Integer.valueOf(args[0]));
                    client.start();
                } catch (NumberFormatException a) {
                    System.out.println("Not a valid Argument");
                    System.exit(0);
                }
                break;
            }
            case 2: {
                String ip = args[0];
                int port = 0;
                InetAddress address = null;
                try {
                    port = Integer.valueOf(args[1]);
                } catch (NumberFormatException a) {
                    System.out.println("Not a valid port");
                    System.exit(0);
                }
                if (Character.isDigit(ip.charAt(0))) {
                    try {
                        address = InetAddress.getByAddress(stringToByte(ip));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        System.out.println("Invalid host");
                        System.exit(0);
                    }
                } else {
                    try {
                        address = InetAddress.getByName(ip);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        System.out.println("Invalid host");
                        System.exit(0);
                    }
                }
                client = new ClientApplication(address, port);
                client.start();
            }
            break;
            default: {
                client = new ClientApplication(8080);
                client.start();
            }
        }

    }

    private static byte[] stringToByte(String s) {
        int ipNumMax = 4;
        Scanner scanner = new Scanner(s).useDelimiter(".");
        byte[] array = new byte[4];
        try {
            for (int i = 0; i <= ipNumMax; i++) {
                array[i] = scanner.nextByte();
            }
        }
        catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            System.out.println("Invalid IP address");
            System.exit(0);
        }
        return array;
    }
}
