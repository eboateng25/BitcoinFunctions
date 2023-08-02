package releasetransaction;

import java.text.DateFormat;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.io.IOException;
import java.text.DecimalFormat;
import org.json.JSONObject;
import java.util.ArrayList;
import org.json.JSONArray;

public class ReleaseTransaction
{

    final static String LOG_FILE = "C:\\logs\\ReleaseTransaction.log";

    public static void main(final String[] args) {
        try {
            final String args2 = args[0];
            final String args3 = args[1];
            final String args4 = args[2];
            final String BITM_ADDRESS = "2NALknEp4xhc1rFkpNoJNKjwkuQeCYp6NDY"; // BITM FEES WALLET CHANGE BEFORE USE
            final String RECEIVE_ADDRESS = args2;
            final float BITCOIN_FEE = 1.0E-4f;
            final float RECEIVING_AMOUNT = Float.parseFloat(args3);
            final Process listunspentP = Runtime.getRuntime().exec("C:\\Program Files\\Bitcoin\\daemon\\bitcoin-cli.exe listunspent");
            final String listunspent = getResultFromProccess(listunspentP);
            final JSONArray obj = new JSONArray(listunspent);
            final ArrayList<String> txid = new ArrayList<String>();
            final ArrayList<String> vout = new ArrayList<String>();
            float amountReceived = 0.0f;
            for (int i = 0; i < obj.length(); ++i) {
                if (obj.getJSONObject(i).get("address").toString().contentEquals(args4)) {
                    txid.add(obj.getJSONObject(i).get("txid").toString());
                    vout.add(obj.getJSONObject(i).get("vout").toString());
                    amountReceived += obj.getJSONObject(i).getFloat("amount");
                }
            }
            final JSONArray txidvouts = new JSONArray();
            for (int j = 0; j < txid.size(); ++j) {
                final JSONObject txidJSON = new JSONObject();
                txidJSON.put("\"txid\"", (Object)("\"" + txid.get(j) + "\""));
                txidJSON.put("\"vout\"", (Object)vout.get(j));
                txidvouts.put((Object)txidJSON);
            }
            final JSONArray outputArray = new JSONArray();
            final NumberFormat formatter = new DecimalFormat("########.########");
            final float BITM_FEE = amountReceived - (BITCOIN_FEE + RECEIVING_AMOUNT);
            outputArray.put((Object)new JSONObject().put("\"" + BITM_ADDRESS + "\"", (Object)formatter.format(BITM_FEE)));
            outputArray.put((Object)new JSONObject().put("\"" + RECEIVE_ADDRESS + "\"", (Object)formatter.format(RECEIVING_AMOUNT)));
            final Process createraw = Runtime.getRuntime().exec("C:\\Program Files\\Bitcoin\\daemon\\bitcoin-cli.exe createrawtransaction \"" + txidvouts + "\" \"" + outputArray + "\"");
            final String hashresult;
            final String createrawoutput = hashresult = getResultFromProccess(createraw);
            final Process signtransaction = Runtime.getRuntime().exec("C:\\Program Files\\Bitcoin\\daemon\\bitcoin-cli.exe signrawtransactionwithwallet " + hashresult);
            final String signtransactionoutput = getResultFromProccess(signtransaction);
            final JSONObject signtransactionobj = new JSONObject(signtransactionoutput);
            final String hex = signtransactionobj.getString("hex");
            final Process broadcastTransaction = Runtime.getRuntime().exec("C:\\Program Files\\Bitcoin\\daemon\\bitcoin-cli.exe sendrawtransaction " + hex);
            final String broadcastTransactionResult = getResultFromProccess(broadcastTransaction);
            System.out.println(broadcastTransactionResult);
        }
        catch (IOException ex) {
            System.out.println("false");
            log(ex.getLocalizedMessage());
            System.exit(0);
        }
    }
    
    public static String getResultFromProccess(final Process p) {
        String result = "";
        try {
            final BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            final BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                try {
                    log(s);
                    result += s;
                }
                catch (Exception e) {
                    System.out.println("false");
                    log(e.getLocalizedMessage());
                    System.exit(0);
                }
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println("false");
                log(s);
                System.exit(0);
            }
        }
        catch (IOException ex) {
            System.out.println("false");
            log(ex.getLocalizedMessage());
            System.exit(0);
        }
        return result;
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
