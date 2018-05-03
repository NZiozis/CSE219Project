package datastructures;

import algorithms.DataSet;

public class ClusteringDrop implements Drop{

    private DataSet output;
    private boolean empty = true;

    @Override
    public synchronized DataSet take(){
        // Wait until the output is available
        while (empty){
            try{
                wait();
            }
            catch (InterruptedException ignore){}
        }
        empty = true;
        notifyAll();
        return output;
    }

    @Override
    public synchronized void put(Object output){
        // Wait until the output has been received
        while (!empty){
            try{
                wait();
            }
            catch (InterruptedException ignore){}
        }
        empty = false;
        this.output = (DataSet) output;
        notifyAll();

    }
}