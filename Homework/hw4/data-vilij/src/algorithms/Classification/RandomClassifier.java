package algorithms.Classification;

import algorithms.Classifier;
import algorithms.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier{

    private static final Random RAND = new Random();
    private final int           maxIterations;
    private final int           updateInterval;
    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;
    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private       DataSet       dataset;

    public RandomClassifier(DataSet dataset, int maxIterations, int updateInterval, boolean tocontinue){
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
    }

    /**
     * A placeholder main method to just make sure this code runs smoothly
     */
    public static void main(String... args) throws IOException{
        DataSet dataset = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
        classifier.run(); // no multithreading yet
    }

    @Override
    public int getMaxIterations(){
        return maxIterations;
    }

    @Override
    public int getUpdateInterval(){
        return updateInterval;
    }

    @Override
    public boolean tocontinue(){
        return tocontinue.get();
    }

    public List<Integer> getOutput(){ return output; }

    @Override
    public void run(){
        for (int i = 1; i <= updateInterval; i++){
            int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int constant = new Double(RAND.nextDouble() * 100).intValue();

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            // put() method here. Check Banerjee slides for this

            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if (i % updateInterval == 0){
                System.out.printf("Iteration number %d: ", i); //
                flush();
            }
            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05){
                System.out.printf("Iteration number %d: ", i);
                flush();
                break;
            }
        }
    }

    // for internal viewing only
    protected void flush(){
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }
}
