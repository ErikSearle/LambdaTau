package security;

import UsefulTools.BigNumberCalc;
import UsefulTools.TypeConverter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Random;

public class Encryptor {

    private long key;
    private static int[] primes = primeNumberGenerator();

    private Encryptor(long key){
        this.key = key;
    }

    public static Encryptor negotiateKeysClientSide(InputStreamReader in, OutputStreamWriter out){

        //Determine p and alpha to use in key exchange
        Random random = new Random();
        int p = primes[random.nextInt(9000)+1000];
        int[] primitiveRootsOfp = findAllPrimitiveRoots(p);
        int alpha = primitiveRootsOfp[random.nextInt(primitiveRootsOfp.length)];

        try {
            //Send p and alpha to server
            out.write(TypeConverter.intToCharArray(p));
            out.flush();
            out.write(TypeConverter.intToCharArray(alpha));
            out.flush();
            //Generate and send partial key
            int x = random.nextInt(p-1) + 1;
            int partialKey = (int) Math.pow(alpha, x)%p;
            out.write(TypeConverter.intToCharArray(partialKey));
            out.flush();

            //Receive partial key from server
            char[] input = new char[4];
            int bytesRead = in.read(input, 0, 4);
            if(bytesRead != 4) throw new IOException();

            int y = TypeConverter.charArrayToInt(input);

            //Create full key from two partial keys and build encryptor
            int key = (int) Math.pow(y, x)%p;
            return new Encryptor(key);

        } catch (IOException e){
            System.exit(1);
        }

        return new Encryptor(-1);
    }

    public static Encryptor negotiateKeysServerSide(InputStreamReader in, OutputStreamWriter out){
        int bytesRead;
        char[] input = new char[4];
        Random random = new Random();
        try{
            //Get p and alpha values from client
            bytesRead = in.read(input, 0, 4);
            if(bytesRead != 4) throw new IOException("Key Exchange Failed");
            int p = TypeConverter.charArrayToInt(input);
            bytesRead = in.read(input, 0, 4);
            if(bytesRead != 4) throw new IOException("Key Exchange Failed");
            int alpha = TypeConverter.charArrayToInt(input);


            //Generate and send partial key
            int x = random.nextInt(p-1) + 1;

            int partialKey = (int) Math.pow(alpha, x)%p;
            out.write(TypeConverter.intToCharArray(partialKey));
            out.flush();

            //Receive partial key from client
            input = new char[4];
            bytesRead = in.read(input, 0, 4);
            if(bytesRead != 4) throw new IOException("Key Exchange Failed");

            int y = TypeConverter.charArrayToInt(input);

            //Create full key from two partial keys and build encryptor
            int key = (int) Math.pow(y, x)%p;
            return new Encryptor(key);

        } catch(IOException e){
            System.exit(1);
        }

        return new Encryptor(-1);
    }

    public char[] encrypt(char[] message){
        for(int i=0; i<message.length-4; i+=4){
            char[] toEncrypt = Arrays.copyOfRange(message, i, i+4);
            char[] encrypted = TypeConverter.intToCharArray((int)key^TypeConverter.charArrayToInt(toEncrypt));
            for(int j=0; j<4; j++){
                message[i+j] = encrypted[j];
            }
        }
        return message;
    }

    public char[] decrypt(char[] message){
        return encrypt(message);
    }

    private static int[] primeNumberGenerator(){
        int[] primesFound = new int[10000];
        primesFound[0] = 2;
        primesFound[1] = 3;
        int counter = 2;
        int currentNumber = 5;
        outer: while(counter < 10000){
            double sqrtCurrentNumber = Math.sqrt(currentNumber);
            int i=1;
            while(primesFound[i] < sqrtCurrentNumber && i < counter){
                if(currentNumber%primesFound[i] == 0){
                    currentNumber += 2;
                    continue outer;
                }
                i++;
            }
            primesFound[counter] = currentNumber;
            currentNumber += 2;
            counter++;
        }
        return primesFound;
    }

    static int[] findAllPrimitiveRoots(int p){
        int[] primitiveRoots = new int[p];
        int counter = 0;
        int lowest = findLowestPrimitiveRoot(p);
        primitiveRoots[counter] = lowest;
        counter++;
        for(int i=2; i<p; i++){
            if(gcd(i, p-1) == 1){
                primitiveRoots[counter] = BigNumberCalc.modPow(lowest, i, p);
                counter ++;
            }
        }
        return Arrays.copyOfRange(primitiveRoots, 0, counter);
    }

    private static int findLowestPrimitiveRoot(int p){
        boolean[] modSpace;
        outer: for(int i=2; i<p; i++){
            modSpace = new boolean[p-1];
            for(int j=1; j<p; j++){
                int x = BigNumberCalc.modPow(i, j, p) - 1;
                if(modSpace[x]) continue outer;
                else modSpace[x] = true;
            }
            return i;
        }
        return -1;
    }

//    static int[] findPrimeFactors(int s){
//        int[] primeFactors = new int[10000];
//        int counter = 0;
//        for(int i=0; primes[i] < Math.sqrt(s); i++){
//            if(s%primes[i] == 0){
//                primeFactors[counter] = primes[i];
//                counter++;
//            }
//        }
//        return Arrays.copyOfRange(primeFactors, 0, counter);
//    }

    private static int gcd(int a, int b){
        if(a == 0) return b;
        else return gcd(b%a, a);
    }
}
