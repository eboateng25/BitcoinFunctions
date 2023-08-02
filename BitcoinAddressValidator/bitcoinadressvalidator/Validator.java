import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.io.IOException;
import java.security.MessageDigest;

package bitcoinaddressvalidator;

public class Validator
{
    private static final char[] ALPHABET;
    private static final int[] INDEXES;
    private static final MessageDigest digest;
    
    public static boolean validate(final String addr) {
        try {
            final int addressHeader = getAddressHeader(addr);
            return addressHeader == 0 || addressHeader == 5;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    private static int getAddressHeader(final String address) throws IOException {
        final byte[] tmp = decodeChecked(address);
        return tmp[0] & 0xFF;
    }
    
    private static byte[] decodeChecked(final String input) throws IOException {
        byte[] tmp = decode(input);
        if (tmp.length < 4) {
            throw new IOException("BTC AddressFormatException Input too short");
        }
        final byte[] bytes = copyOfRange(tmp, 0, tmp.length - 4);
        final byte[] checksum = copyOfRange(tmp, tmp.length - 4, tmp.length);
        tmp = doubleDigest(bytes);
        final byte[] hash = copyOfRange(tmp, 0, 4);
        if (!Arrays.equals(checksum, hash)) {
            throw new IOException("BTC AddressFormatException Checksum does not validate");
        }
        return bytes;
    }
    
    private static byte[] doubleDigest(final byte[] input) {
        return doubleDigest(input, 0, input.length);
    }
    
    private static byte[] doubleDigest(final byte[] input, final int offset, final int length) {
        synchronized (Validator.digest) {
            Validator.digest.reset();
            Validator.digest.update(input, offset, length);
            final byte[] first = Validator.digest.digest();
            return Validator.digest.digest(first);
        }
    }
    
    private static byte[] decode(final String input) throws IOException {
        if (input.length() == 0) {
            return new byte[0];
        }
        final byte[] input2 = new byte[input.length()];
        for (int i = 0; i < input.length(); ++i) {
            final char c = input.charAt(i);
            int digit58 = -1;
            if (c >= '\0' && c < '\u0080') {
                digit58 = Validator.INDEXES[c];
            }
            if (digit58 < 0) {
                throw new IOException("Bitcoin AddressFormatException Illegal character " + c + " at " + i);
            }
            input2[i] = (byte)digit58;
        }
        int zeroCount;
        for (zeroCount = 0; zeroCount < input2.length && input2[zeroCount] == 0; ++zeroCount) {}
        final byte[] temp = new byte[input.length()];
        int j = temp.length;
        int startAt = zeroCount;
        while (startAt < input2.length) {
            final byte mod = divmod256(input2, startAt);
            if (input2[startAt] == 0) {
                ++startAt;
            }
            temp[--j] = mod;
        }
        while (j < temp.length && temp[j] == 0) {
            ++j;
        }
        return copyOfRange(temp, j - zeroCount, temp.length);
    }
    
    private static byte divmod256(final byte[] number58, final int startAt) {
        int remainder = 0;
        for (int i = startAt; i < number58.length; ++i) {
            final int digit58 = number58[i] & 0xFF;
            final int temp = remainder * 58 + digit58;
            number58[i] = (byte)(temp / 256);
            remainder = temp % 256;
        }
        return (byte)remainder;
    }
    
    private static byte[] copyOfRange(final byte[] source, final int from, final int to) {
        final byte[] range = new byte[to - from];
        System.arraycopy(source, from, range, 0, range.length);
        return range;
    }
    
    static {
        ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
        INDEXES = new int[128];
        try {
            digest = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < Validator.INDEXES.length; ++i) {
            Validator.INDEXES[i] = -1;
        }
        for (int i = 0; i < Validator.ALPHABET.length; ++i) {
            Validator.INDEXES[Validator.ALPHABET[i]] = i;
        }
    }
}
