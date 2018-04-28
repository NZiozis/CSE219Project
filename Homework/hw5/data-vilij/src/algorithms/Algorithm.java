package algorithms;

/**
 * This interface provides a way to run an algorithm
 * on a thread as a {@link java.lang.Runnable} object.
 *
 * @author Ritwik Banerjee
 */
public interface Algorithm extends Runnable{

    int getMaxIterations();

    int getUpdateInterval();

    boolean tocontinue();

    @Override
    default void run(){
        System.out
                .println("This algorithm doesn't have a run method yet. Please restart the application and try again.");
        System.exit(0);
    }
}
