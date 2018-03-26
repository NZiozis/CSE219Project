/**
 * @author Ritwik Banerjee
 */
public class TransferHandler implements Runnable {

    public static final double MAX   = 1000;
    public static final int    DELAY = 100;

    private SampleBank sampleBank;
    private int        fromAccount;

    public TransferHandler(SampleBank sampleBank, int fromAccount) {
        this.sampleBank = sampleBank;
        this.fromAccount = fromAccount;
    }

    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                int    toAccount = (int) (SampleBank.NUMBER_OF_ACCOUNTS * Math.random());
                double amount    = MAX * Math.random();
                sampleBank.transfer(fromAccount, toAccount, amount);
                Thread.sleep((int) (DELAY * Math.random()));
            }
        } catch (InterruptedException e) { /* squelch */ }
    }
}
