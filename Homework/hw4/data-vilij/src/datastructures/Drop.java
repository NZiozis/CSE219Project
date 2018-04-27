package datastructures;

import java.util.List;

public class Drop{
    private List<Integer> output;
    private boolean empty = true;


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

    public synchronized void put(List<Integer> output){
        // Wait until the output has been received
        while (!empty){
            try{
                wait();
            }
            catch (InterruptedException ignore){}
        }
        empty = false;
        this.output = output;
        notifyAll();
    }
}
