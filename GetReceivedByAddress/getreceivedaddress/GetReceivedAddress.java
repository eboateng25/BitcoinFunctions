package getreceivedbyaddress;

import java.text.DateFormat;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GetReceivedAddress
{


    final static String LOG_FILE = "C:\\logs\\ReleaseTransaction.log";

    public static void main(final String[] args) {
        String s = null;
        final String address = args[0];
        final String confirmations = args[1];
        try {
            final Process p = Runtime.getRuntime().exec("C:\\Program Files\\Bitcoin\\daemon\\bitcoin-cli.exe getreceivedbyaddress " + address + " " + confirmations);
            log("C:\\Program Files\\Bitcoin\\daemon\\bitcoin-cli.exe getreceivedbyaddress " + address + " " + confirmations);
            final BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            final BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((s = stdInput.readLine()) != null) {
                try {
                    log(s);
                    System.out.println(s);
                }
                catch (Exception e) {
                    System.out.println("false");
                    log(e.getLocalizedMessage());
                }
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println("false");
                log(s);
            }
            System.exit(0);
        }
        catch (IOException e2) {
            log(e2.getLocalizedMessage());
            System.exit(-1);
        }
    }
    
    public static void log(final String logText) {
        try {
            String textToAppend = logText;
            final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            final Date date = new Date();
            textToAppend = "[" + dateFormat.format(date) + "] " + textToAppend;
            final BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE , true));
            writer.newLine();
            writer.write(textToAppend);
            writer.close();
        }
        catch (Exception ex) {}
    }
}
