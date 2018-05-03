package datastructures;

import java.util.List;

public class ClassificationDrop implements Drop{
    private List<Integer> output;
    private boolean empty = true;

    @Override
    public synchronized List<Integer> take(){
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
        this.output = (List<Integer>) output;
        notifyAll();
    }
}
