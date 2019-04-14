package UsefulTools;

import java.nio.ByteBuffer;

public class TypeConverter {

    public static char[] intToCharArray(int n){
        byte[] intAsBytes = ByteBuffer.allocate(4).putInt(n).array();
        char[] intAsChars = new char[4];
        for(int i=0; i<4; i++) intAsChars[i] = (char) intAsBytes[i];
        return intAsChars;
    }

    public static int charArrayToInt(char[] array){
        byte[] convertedToBytes = new byte[4];
        for(int i=0; i<4; i++) convertedToBytes[i] = (byte) array[i];
        return ByteBuffer.wrap(convertedToBytes).getInt();
    }
}
