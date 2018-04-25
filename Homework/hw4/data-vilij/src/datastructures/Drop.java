package datastructures;

import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.util.List;

public class Drop{
    private List<Integer> output;
    private boolean empty = true;
    private ApplicationTemplate applicationTemplate;

    public Drop(ApplicationTemplate applicationTemplate){
        this.applicationTemplate = applicationTemplate;
    }


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
        ( (AppUI) applicationTemplate.getUIComponent() ).isRunningProperty().set(true);
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
        ( (AppUI) applicationTemplate.getUIComponent() ).isRunningProperty().set(false);
        notifyAll();
    }
}
