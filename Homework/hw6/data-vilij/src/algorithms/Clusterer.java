package algorithms;

import datastructures.Drop;

/**
 * @author Ritwik Banerjee
 */
public abstract class Clusterer implements Algorithm{

    protected final int  numberOfClusters;
    protected       Drop drop;

    public Clusterer(int k){
        if (k < 2){ k = 2; }
        else if (k > 4) k = 4;
        numberOfClusters = k;
    }

    public int getNumberOfClusters(){ return numberOfClusters; }
}