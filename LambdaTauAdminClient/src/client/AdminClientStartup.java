package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class AdminClientStartup {
    public static void main(String[] args) {
        AdminClientApplication client = null;
        switch (args.length) {
            case 2: {
                int port = 0;
                try {
                    port = Integer.valueOf(args[0]);
                } catch(NumberFormatException e){
                    System.out.println("Invalid port added to arguments");
                    System.exit(0);
                }
                String password = String.valueOf(args[1]);
                try {
                    client = new AdminClientApplication(port, password);
                } catch (NumberFormatException a) {
                    System.out.println("Not a valid Argument");
                    System.exit(0);
                }
                break;
            }
            case 3: {
                String ip = args[0];

                int port = 0;
                InetAddress address = null;
                try {
                    port = Integer.valueOf(args[1]);
                } catch (NumberFormatException a) {
                    System.out.println("Not a valid port");
                    System.exit(0);
                }
                String password = args[2];
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
                client = new AdminClientApplication(address, port, password);

            }
            break;
            default: {
                int port = -1;
                InetAddress address = null;
                boolean getInfo = true;
                System.out.println("No IP, port, or password information found");
                while (getInfo) {
                    System.out.println("Please type desired IP or host name, 0 if localhost");
                    Scanner scanner = new Scanner(System.in);
                    String ip = scanner.next();
                    System.out.println("Please type desired port, 0 if default (8081)");
                    String portString = scanner.next();
                    try {
                        port = Integer.valueOf(portString);
                    } catch (NumberFormatException a) {
                        System.out.println("Not a valid port");
                        port = -1;
                    }
                    System.out.println("Please enter the server admin password, 0 if default (1111)");
                    String password = scanner.next();
                    if (ip.charAt(0) == '0' && port != 0 ) {
                        client = new AdminClientApplication(port,password);
                        getInfo = false;

                    } else if (ip.charAt(0) == '0' && port == 0 && password.equals("0")) {
                        client = new AdminClientApplication(8081,"1111");
                        getInfo = false;
                    } else if (Character.isDigit(ip.charAt(0)) && port > 0 && password != "") {
                        try {
                            address = InetAddress.getByAddress(stringToByte(ip));
                            client = new AdminClientApplication(address, port, password);
                            getInfo = false;
                        } catch (UnknownHostException e) {
                        }
                    } else {
                        if (Character.isAlphabetic(ip.charAt(0)) && port > 0 && password != "") {
                            try {
                                address = InetAddress.getByName(ip);
                                client = new AdminClientApplication(address, port,password);
                                getInfo = false;
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                                System.out.println("Invalid host");
                            }
                        }
                    }
                }
            }
        }
        client.start();
    }

    private static byte[] stringToByte(String s) {
        int ipNumMax = 4;
        Scanner scanner = new Scanner(s).useDelimiter(".");
        byte[] array = new byte[4];
        try {
            for (int i = 0; i <= ipNumMax; i++) {
                array[i] = scanner.nextByte();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            System.out.println("Invalid IP address");
        }
        return array;
    }
}
