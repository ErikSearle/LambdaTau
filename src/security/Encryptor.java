package security;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Encryptor {

    private long key;

    private Encryptor(long key){
        this.key = key;
    }

    public static Encryptor negotiateKeys(InputStreamReader in, OutputStreamWriter out){
        return new Encryptor(-1);
    }

    public char[] encrypt(char[] message){
        return message;
    }

    public char[] decrypt(char[] message){
        return message;
    }
}
