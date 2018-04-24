package datastructures;

import javafx.beans.property.SimpleBooleanProperty;

import java.util.List;

public class Drop{
    private List<Integer> output;
    private SimpleBooleanProperty empty = new SimpleBooleanProperty(true);

    public SimpleBooleanProperty emptyProperty(){
        return empty;
    }

    public boolean isEmpty(){
        return empty.get();
    }

    public synchronized List<Integer> take(){
        // Wait until the output is available
        while (empty.get()){
            try{
                wait();
            }
            catch (InterruptedException ignore){}
        }
        empty.set(true);
        notifyAll();
        System.out.println(output.toString());
        return output;
    }

    public synchronized void put(List<Integer> output){
        // Wait until the output has been received
        while (!empty.get()){
            try{
                wait();
            }
            catch (InterruptedException ignore){}
        }
        empty.set(false);
        this.output = output;
        notifyAll();
    }
}
