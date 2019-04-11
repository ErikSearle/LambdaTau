package UsefulTools;

public class MessageRework implements Comparable<MessageRework> {
    // message is always in a single int type, sender ID, message format
    // int type:
    // 0 for system commands like kick / ban / online
    // 1 for slashcommands like /uptime
    // 2 for normal messages

    private int messageType;
    private int senderID;
    private String message;
    private String command;

    public MessageRework(char[] chars) {
        this(new String(chars));
    }

    public MessageRework(String raw) {
        switch (raw.charAt(0)) {
            
        }
    }

    static MessageRework newMessageParse(char[] chars, int ID) {

    }

    @Override
    public int compareTo(MessageRework messageRework) {
        return 0;
    }
}
