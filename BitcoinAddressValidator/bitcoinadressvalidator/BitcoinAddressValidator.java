package bitcoinaddressvalidator;

public class BitcoinAddressValidator
{
    public static void main(final String[] args) {
        System.out.println(Validator.validate(args[0]));
    }
}
