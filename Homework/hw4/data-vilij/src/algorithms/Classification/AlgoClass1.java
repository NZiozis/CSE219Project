package algorithms.Classification;

import algorithms.Classifier;

public class AlgoClass1 extends Classifier{
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

    @Override
    public void run(){

    }
}
