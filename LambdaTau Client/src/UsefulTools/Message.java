package UsefulTools;

public class Message implements Comparable<Message> {
    // message is always in a single int type, sender ID, message format
    // int type:
    // 0 for system commands like kick / ban / online
    // 1 for normal messages
    // 2 for slashcommands like /uptime

    private int messageType;
    private int senderID;
    private String message = "";
    private String command = "";
    private String arguments = "";
    private String fullMessage = "";

    public Message(char[] chars) {
        this(new String(chars));
    }

    public Message() {
        messageType = -1;
        senderID = -1;
    }

    public Message(String raw) {
        fullMessage = raw;
        messageType = Integer.parseInt(raw.substring(0, 1));
        raw = raw.substring(2);
        int IDpos = raw.indexOf(" ");
        String sender = raw.substring(0, IDpos);
        senderID = Integer.parseInt(sender);
        raw = raw.substring(IDpos + 1);
        switch (messageType) {
            case 0:
                parseSystem(raw);
                break;
            case 1:
                parseMessage(raw);
                break;
            case 2:
                parseSlash(raw);
                break;
            default:
                throw new IllegalArgumentException("unsupported messagetype");
        }
    }

    public static Message newMessageParse(String s, int ID) {
        String raw = s;
        String builtString;
        if (raw.charAt(0) == '/') {
            builtString = 2 + " " + ID + " " + raw;
        } else builtString = 1 + " " + ID + " " + raw;
        return new Message(builtString);
    }

    public static Message newMessageParse(char[] chars, int ID) {
        return newMessageParse(new String(chars), ID);
    }

    public boolean isEmpty() {
        return messageType == -1;
    }

    public int getSenderID() {
        return senderID;
    }

    public String getMessage() {
        return message.trim();
    }

    public void addPrefix(String s) {
        message = s + " " + message;
        rebuildFullMessage();
    }

    public String getCommand() {
        return command;
    }

    public String getArguments() {
        return arguments.trim();
    }

    public void setArguments(String s) {
        arguments = s;
        rebuildFullMessage();
    }

    public char[] toCharArray() {
        return fullMessage.toCharArray();
    }

    @Override
    public int compareTo(Message messageRework) {
        int returnNum = 0;
        if (this.senderID < messageRework.senderID) {
            returnNum = 1;
        }
        return returnNum;
    }

    private void parseSystem(String s) {
        int pos = s.indexOf(" ");
        String system = s.substring(0, pos);
        switch (system) {
            case "pmsg:":
                system = s;
                pos = system.indexOf(" ");
                String temp = system.substring(0, pos + 1);
                command = temp.trim();
                system = system.replace(temp, "");
                int pos2 = system.indexOf(" ");
                temp = system.substring(0, pos2 + 1);
                arguments = temp.trim();
                temp = system.substring(pos2);
                message = temp.trim();
                break;
            case "quit:":
            case "online:":
                command = system;
                break;
            case "kick:":
            case "name:":
                system = s;
                pos = system.indexOf(" ");
                temp = system.substring(0, pos);
                command = temp.trim();
                temp = system.substring(pos);
                arguments = temp.trim();
                break;
        }
    }

    private void parseSlash(String s) {
        String system = s;
        String temp = "";
        int pos = system.indexOf(" ");
        if (pos != -1) {
            temp = system;
            system = system.substring(0, pos);
            temp = temp.substring(pos + 1);
        }
        switch (system) {
            case "/quit":
            case "/rename":
            case "/online":
            case "/help":
            case "/uptime":
                command = system;
                break;
            case "/msg":
                command = system;
                system = temp;
                pos = system.indexOf(" ");
                temp = system.substring(0, pos);
                arguments = temp;
                temp = system.substring(pos + 1);
                message = temp;
                break;
        }
    }

    private void parseMessage(String s) {
        message = s.trim();
    }

    public void toSysCommand() {
        switch (command) {
            case "/quit":
                command = "quit:";
                break;
            case "/msg":
                command = "pmsg:";
                break;
            case "/online":
                command = "online:";
                break;
            case "/rename":
                command = "name:";
                break;
        }
        messageType = 0;
        rebuildFullMessage();

    }

    private void rebuildFullMessage() {
        String string = messageType + " " + senderID + " " + command + " " + arguments + " " + message;
        string = string.replaceAll("\\s{2,}", " ");
        fullMessage = string;
    }

    public boolean isSlashCommand() {
        return messageType == 2;
    }

    public boolean isSystemCommand() {
        return messageType == 0;
    }

    public boolean isMessage() {
        return messageType == 1;
    }


    @Override
    public String toString() {
        rebuildFullMessage();
        return fullMessage;
    }
}