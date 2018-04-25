package algorithms.Classification;

import algorithms.Classifier;
import algorithms.DataSet;
import datastructures.Drop;

public class Small extends Classifier{

    public Small(DataSet dataset, Drop drop, int maxIterations, int updateInterval, boolean toContinue){}

    @Override
    public int getMaxIterations(){
        return 0;
    }

    @Override
    public int getUpdateInterval(){
        return 0;
    }

    @Override
    public boolean tocontinue(){
        return false;
    }
}
