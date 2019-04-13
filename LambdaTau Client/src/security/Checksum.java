package security;

public class Checksum {

    /**
     * Very simple 1 byte checksum calculator. Simply adds the values of all the characters in the message. Any time the
     * sum exceeds the space of one byte, the additional bit is wrapped around and added back onto the least significant
     * bit
     * @param message The input message
     * @return calcualted checksum
     */
    public static int calculateCheckSum(char[] message){
        int checksum = 0;
        for(int i=0; i<message.length; i++){
            checksum += message[i];
            while(checksum > 255){
                checksum -= 255;
            }
        }
        return checksum;
    }
}
