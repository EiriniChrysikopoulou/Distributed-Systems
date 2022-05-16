package Utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Utils {
    public static BigInteger MD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            return new BigInteger(1, messageDigest);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);

        }
    }
}