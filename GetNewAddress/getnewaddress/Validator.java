package getnewaddress;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.util.Arrays;

public class Validator
{
    private static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    
    public static boolean validate(final String addr) {
        if (addr.length() < 26 || addr.length() > 35) {
            return false;
        }
        final byte[] decoded = decodeBase58To25Bytes(addr);
        if (decoded == null) {
            return false;
        }
        final byte[] hash1 = sha256(Arrays.copyOfRange(decoded, 0, 21));
        final byte[] hash2 = sha256(hash1);
        return Arrays.equals(Arrays.copyOfRange(hash2, 0, 4), Arrays.copyOfRange(decoded, 21, 25));
    }
    
    private static byte[] decodeBase58To25Bytes(final String input) {
        BigInteger num = BigInteger.ZERO;
        for (final char t : input.toCharArray()) {
            final int p = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".indexOf(t);
            if (p == -1) {
                return null;
            }
            num = num.multiply(BigInteger.valueOf(58L)).add(BigInteger.valueOf(p));
        }
        final byte[] result = new byte[25];
        final byte[] numBytes = num.toByteArray();
        System.arraycopy(numBytes, 0, result, result.length - numBytes.length, numBytes.length);
        return result;
    }
    
    private static byte[] sha256(final byte[] data) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            return md.digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
