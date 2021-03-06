package algorithms.Clustering;

import algorithms.Clusterer;
import algorithms.DataSet;
import datastructures.ClusteringDrop;
import javafx.geometry.Point2D;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Ritwik Banerjee
 */
public class KMeansClusterer extends Clusterer{

    private final int           updateInterval;
    private final AtomicBoolean tocontinue;
    private       int           maxIterations;
    private       DataSet       dataset;
    private       List<Point2D> centroids;


    public KMeansClusterer(DataSet dataset, ClusteringDrop drop, int maxIterations, int updateInterval,
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
        initializeCentroids();
        int iteration = 0;
        while (iteration++ < maxIterations & tocontinue.get()){
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
            recomputeCentroids();
        }
        maxIterations -= ( iteration - 1 );
    }

    private void initializeCentroids(){
        Set<String> chosen = new HashSet<>();
        List<String> instanceNames = new ArrayList<>(dataset.getLabels().keySet());
        Random r = new Random();
        while (chosen.size() < numberOfClusters){
            int i = r.nextInt(instanceNames.size());
            while (chosen.contains(instanceNames.get(i))) i = ( ++i % instanceNames.size() );
            chosen.add(instanceNames.get(i));
        }
        centroids = chosen.stream().map(name -> dataset.getLocations().get(name)).collect(Collectors.toList());
        tocontinue.set(true);
    }

    private void assignLabels(){
        dataset.getLocations().forEach((instanceName, location) -> {
            double minDistance = Double.MAX_VALUE;
            int minDistanceIndex = -1;
            for (int i = 0; i < centroids.size(); i++){
                double distance = computeDistance(centroids.get(i), location);
                if (distance < minDistance){
                    minDistance = distance;
                    minDistanceIndex = i;
                }
            }
            dataset.getLabels().put(instanceName, Integer.toString(minDistanceIndex));
        });
    }

    /**
     * The addition that I made here was that if the tocontinue isn't updated to true I have the algorithm produce a
     * null data set. This makes it so that it stops running (as this is my end condition) and terminates the algorithm
     * run
     */
    private void recomputeCentroids(){
        tocontinue.set(false);
        IntStream.range(0, numberOfClusters).forEach(i -> {
            AtomicInteger clusterSize = new AtomicInteger();
            Point2D sum =
                    dataset.getLabels().entrySet().stream().filter(entry -> i == Integer.parseInt(entry.getValue()))
                           .map(entry -> dataset.getLocations().get(entry.getKey()))
                           .reduce(new Point2D(0, 0), (p, q) -> {
                               clusterSize.incrementAndGet();
                               return new Point2D(p.getX() + q.getX(), p.getY() + q.getY());
                           });
            Point2D newCentroid = new Point2D(sum.getX() / clusterSize.get(), sum.getY() / clusterSize.get());
            if (!newCentroid.equals(centroids.get(i))){
                centroids.set(i, newCentroid);
                tocontinue.set(true);
            }
            else{
                drop.put(null);
            }
        });
    }

}