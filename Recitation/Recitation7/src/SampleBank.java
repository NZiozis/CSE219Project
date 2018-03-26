import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Ritwik Banerjee
 */
public class SampleBank {

    static double INITIAL_BALANCE = 1000;
    static int NUMBER_OF_ACCOUNTS = 100;

    private final Map<Integer, Double> accounts;
    private ReentrantLock bankLock = new ReentrantLock();

    public SampleBank() {
        accounts = IntStream.range(0, NUMBER_OF_ACCOUNTS)
                .boxed()
                .collect(Collectors.toMap(Function.identity(), i -> INITIAL_BALANCE));
        printTotalAmount();
    }

    /*public void transfer(int fromAccount, int toAccount, double balance) {
        bankLock.lock();
        try {
            if (accounts.get(fromAccount) < balance) {
                return;
            }
            accounts.put(fromAccount, accounts.get(fromAccount) - balance);
            System.out.printf("\t\t(Thread.currentThread()) Transferred %10.2f from Account %d to Account %d%n",
                    balance,
                    fromAccount,
                    toAccount);
            accounts.put(toAccount, accounts.get(toAccount) + balance);
            printTotalAmount();
        } finally {
            bankLock.unlock();
        }
    }*/

    public synchronized void transfer(int fromAccount, int toAccount, double balance) {
        if (accounts.get(fromAccount) < balance) {
            return;
        }
        accounts.put(fromAccount, accounts.get(fromAccount) - balance);
        System.out.printf("\t\t(Thread.currentThread()) Transferred %10.2f from Account %d to Account %d%n",
                balance,
                fromAccount,
                toAccount);
        accounts.put(toAccount, accounts.get(toAccount) + balance);
        printTotalAmount();

    }

    public void printTotalAmount() {
        System.out.printf("Total amount in SampleBank Inc. = %10.2f%n",
                accounts.values().stream().reduce(0d, (total, current) -> total + current));
    }
}
