package UsefulTools;

public class Message {
    private int senderID;
    private String receiver;
    private boolean slashCommand; //inside application
    private boolean sysInfo;      //for connection returning pass/fails
    private boolean privCommand;  // used for /online /name
    private String slashCommandType;
    private String privCommandType;
    private String message;
    private String original;

    public Message(String raw) {
        original = raw;
        if (raw.charAt(0) == '/') {
            int pos = raw.indexOf(" ");
            slashCommandType = raw.substring(0, pos);
            slashCommand = true;
            privCommand = false;
        } else {
            int pos = raw.indexOf(" ");
            String value = raw.substring(0, pos);
            senderID = Integer.parseInt(value);
            raw = raw.replace(value + " ", "");
            if (raw.contains("**&**!^&@")) {
                privCommand = true;
                slashCommand = false;
                raw = raw.replace("**&**!^&@", "");
                pos = raw.indexOf(":");
                privCommandType = raw.substring(0, pos + 1);
                if (privCommandType.equals("pmsg:")) {
                    raw = raw.replace(privCommandType, "");
                    pos = raw.indexOf(" ");
                    if (pos > -1) {
                        receiver = raw.substring(0, pos);
                        message = raw.replace(receiver, "");
                    } else {
                        receiver = raw;
                        message = "";
                    }
                } else if (privCommandType.equals("name:")) {
                    receiver = raw.replace(privCommandType, "");
                }
            } else message = raw.substring(pos);
        }
    }

    public Message(char[] chars) {
        this(new String(chars));

    }

    public Message() {
        original = null;
        senderID = -5;
        receiver = null;
        slashCommand = false;
        sysInfo = false;
        privCommand = false;
        slashCommandType = null;
        message = null;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public boolean isSlashCommand() {
        return slashCommand;
    }

    public void setSlashCommand(boolean slashCommand) {
        this.slashCommand = slashCommand;
    }

    public boolean isSysInfo() {
        return sysInfo;
    }

    public void setSysInfo(boolean sysInfo) {
        this.sysInfo = sysInfo;
    }

    public boolean isPrivCommand() {
        return privCommand;
    }

    public void setPrivCommand(boolean privCommand) {
        this.privCommand = privCommand;
    }

    public String getSlashCommandType() {
        return slashCommandType;
    }

    public void setSlashCommandType(String slashCommandType) {
        this.slashCommandType = slashCommandType;
    }

    public String getPrivCommandType() {
        return privCommandType;
    }

    public void setPrivCommandType(String privCommandType) {
        this.privCommandType = privCommandType;
    }

    public String getMessage() {
        return message;
    }

    public char[] getMessageChars() {
        return message.toCharArray();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

}