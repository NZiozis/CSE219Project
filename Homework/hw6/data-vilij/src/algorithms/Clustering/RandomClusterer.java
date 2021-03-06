package algorithms.Clustering;

import algorithms.Clusterer;
import algorithms.DataSet;
import datastructures.ClusteringDrop;
import javafx.geometry.Point2D;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClusterer extends Clusterer{

    private final int           updateInterval;
    private final AtomicBoolean tocontinue;
    private       int           maxIterations;
    private       DataSet       dataset;


    public RandomClusterer(DataSet dataset, ClusteringDrop drop, int maxIterations, int updateInterval,
                           int numberOfClusters){
        super(numberOfClusters);
        this.drop = drop;
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(false);
    }

    private static double computeDistance(Point2D p, Point2D q){
        return Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
    }

    @Override
    public int getMaxIterations(){ return maxIterations; }

    @Override
    public int getUpdateInterval(){ return updateInterval; }

    @Override
    public boolean tocontinue(){ return tocontinue.get(); }

    @SuppressWarnings("Duplicates")
    @Override
    public void run(){
        int iteration = 0;
        tocontinue.set(true);
        while (iteration++ < maxIterations && tocontinue.get()){
            assignLabels();
            if (iteration % updateInterval == 0 || iteration >= maxIterations){
                tocontinue.set(false);

                drop.put(dataset);

                try{
                    Thread.sleep(800);
                }
                catch (InterruptedException ignored){}

                if (iteration >= maxIterations) drop.put(null);
            }
        }
        maxIterations -= ( iteration - 1 );

    }

    private void assignLabels(){
        dataset.getLocations().forEach((instanceName, location) -> {
            dataset.getLabels().put(instanceName, Integer.toString((int) ( Math.random() * numberOfClusters )));
        });
    }


}
