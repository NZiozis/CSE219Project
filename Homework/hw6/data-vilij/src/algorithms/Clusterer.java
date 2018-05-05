package algorithms;

import datastructures.ClusteringDrop;

/**
 * @author Ritwik Banerjee
 */
public abstract class Clusterer implements Algorithm{

    protected final int            numberOfClusters;
    protected       ClusteringDrop drop;

    public Clusterer(int k){
        if (k < 2){ k = 2; }
        else if (k > 4) k = 4;
        numberOfClusters = k;
    }

    public int getNumberOfClusters(){ return numberOfClusters; }
}