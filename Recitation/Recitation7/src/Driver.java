/**
 * @author Ritwik Banerjee
 */
public class Driver {
    public static void main(String... args) {
        SampleBank sampleBank = new SampleBank();
        for (int fromAccount = 0; fromAccount < SampleBank.NUMBER_OF_ACCOUNTS; fromAccount++) {
            TransferHandler transferHandler = new TransferHandler(sampleBank, fromAccount);
            Thread          transferThread  = new Thread(transferHandler);
            transferThread.start();
        }
    }
}
