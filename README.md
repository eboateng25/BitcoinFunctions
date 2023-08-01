# Bitcoin Functions
<b>These applications were developed to work on the server side of BitInTheMiddle.co (inactive). These application use and require Bitcoin Core to send and confirm bitcoin transactions.</b>

<i>Please note that this application was designed and developed in April of 2020 and has not been updated since.</i>
 
 - <b>GetRecievedByAddress</b> - Returns the total amount of bitcoin received by the given address in transactions with at least <i>minconf</i> confirmations.
 - <b>BitcoinAddressValidator</b> - Verifies whether the P2PKH bitcoin address provided is valid.
 - <b>GetNewAddress</b> - Returns a new Bitcoin address for receiving payments.
 - <b>ReleaseTransaction</b> - Creates and sends Bitcoins to another address/s.

# <img src="https://imgur.com/xWZInQn.png" width=3%> BitInTheMiddle?
To gain a better understanding of these applications, you would probably benefit from a better understanding of what the BitInTheMiddle (BITM) project was. BITM was a website launched in August 2020 that acted as a Bitcoin escrow. The website would involve two parties, one of which was selling a service or good that would be exchanged for Bitcoin. Both parties would agree to use BITM as their escrow and would require the party selling their service or goods to create a transaction on the BITM network. Once the transaction was created, the other party would send the Bitcoin to BITM and would prompt the seller to send their part of the transaction. Once both parties were happy, the buyer would 'Release the bitcoin', automatically sending the Bitcoin to the seller with a small fee sent to BITM as a cost for using the service. If one of the parties was not happy with the transaction, the dispute button could be used to hold the Bitcoin with BITM allowing for a member of the BITM staff to intervene if necessary.

<p align="center">
<img src="https://i.imgur.com/JbO1TPw.png" width=90%>
</p>
<p align="center">
<i>This image shows an example of what the person buying a service or good would see before making a payment. (Hopefully, this makes more clear what the BitInTheMiddle project was!)</i>
</p>

 
## Building & Usage

Please note that this project has not been updated since 2020. Future work may be done in the future to make sure these functions are more stable.

## Prerequisites
- Java 1.8 + (JDK if building | JRE should be good enough to run the .jar binaries)
- Bitcoin Core version 0.19.1 (This was the version used for production at the time. You will need a fully synced Bitcoin core which can take days or maybe weeks to complete. Newer versions have not been yet tested so I am unable to confirm compatibility with other versions)
  https://bitcoincore.org/bin/bitcoin-core-0.19.1/
  
## Running
Applications are built in the same way any other Java application is built, or you may choose to the prebuilt jar files. Note that the source code has been produced using a decompiler due to lost source code so the project may lack comments.

### GetRecievedByAddress
java -jar GetRecievedByAddress [address] [minconf]

<b>Argument #1 - address - Type: string, required - The Bitcoin address for transactions.</b>

<b>Argument #2 - minconf - Type: numeric, required - Transactions confirmed at least this many times.</b>

This function was used to update the status of each transaction in BITM. The server would run this function for all open transactions every 30s (inexpensive operation) and update the transaction status from either 'waiting for payment', 'waiting for confirmations (Seller had a choice to wait for 1,3,6 confirmations for the transaction)' or 'paid'. 

The total amount in BTC received at this address is returned. It is important on the server application to check that this result is >= to the amount that is needed to be paid to ensure that users will be allowed to pay with separate payments. This value would be subtracted from the amount to pay to update users of the new amount each time.

Example:

`java -jar GetRecievedByAddress 1JUfbTeXxengDPqWSqmHHSVGRSBFPm 3`

### BitcoinAddressValidator
java -jar BitcoinAddressValidator [address]

<b>Argument #1 - address - Type: string, required - The Bitcoin address for validation.</b>

This function was used to verify whether the P2PKH Bitcoin address provided by the seller was a valid address. As of writing this, I am aware that P2PKH legacy addresses are not as commonly used making this function outdated. Plans to update this function to check for P2SH or Compatibility Address Format and Bech32 or Segwit Address Format will be in works for the future.

Example:

`java -jar GetRecievedByAddress 1JUfbTeXxengDPqWSqmHHSVGRSBFPm 3`


Emmanuel Boateng  / eboateng25
