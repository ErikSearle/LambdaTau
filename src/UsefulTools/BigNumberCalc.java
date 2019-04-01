package UsefulTools;

import java.math.BigInteger;

public class BigNumberCalc {

    public static int modPow(int base, int exponent, int mod){
        BigInteger b = BigInteger.valueOf(base);
        BigInteger e = BigInteger.valueOf(exponent);
        BigInteger m = BigInteger.valueOf(mod);
        return b.modPow(e, m).intValue();
    }
}
