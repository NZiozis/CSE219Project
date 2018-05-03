package algorithms.Classification;

import algorithms.Classifier;
import algorithms.DataSet;
import datastructures.ClassificationDrop;
import datastructures.Drop;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier{

    private static final Random RAND = new Random();
    private final int           updateInterval;
    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;
    private       int           maxIterations;
    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private       DataSet       dataset;

    public RandomClassifier(DataSet dataset, Drop drop, int maxIterations, int updateInterval, int tocontinue){
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue == 1);
        this.drop = (ClassificationDrop) drop;
    }

    /**
     * A placeholder main method to just make sure this code runs smoothly
     */
    /*public static void main(String... args) throws IOException{
        DataSet dataset = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
        RandomClassifier classifier = new RandomClassifier(dataset, new Drop(), 100, 5, true);
        classifier.run(); // no multithreading yet
    }*/
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

    @Override
    public void run(){
        for (int i = 1; i <= updateInterval && i <= maxIterations; i++){
            int xCoefficient = (int) ( -1 * Math.round(( 2 * RAND.nextDouble() - 1 ) * 10) );
            int yCoefficient = 10;
            int constant = RAND.nextInt(11);

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if (i % updateInterval == 0 || i >= maxIterations){
                drop.put(output);

                try{
                    Thread.sleep(500);
                }
                catch (InterruptedException ignored){}

                if (i >= maxIterations) drop.put(null);

                System.out.printf("Iteration number %d: ", i); //
                flush();
            }
        }
        maxIterations -= updateInterval;
    }

    // for internal viewing only
    protected void flush(){
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }
}
