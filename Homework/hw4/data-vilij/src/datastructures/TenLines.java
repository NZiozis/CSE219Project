package datastructures;

import java.util.ArrayList;

@SuppressWarnings({"unchecked", "unused"})
public class TenLines<T>{
    private ArrayList<T> activeArea;
    private ArrayList<T> passiveArea;
    private ArrayList<T> totalData;


    public TenLines(){
        activeArea = new ArrayList<>();
        passiveArea = new ArrayList<>();
        totalData = new ArrayList<>();
    }

    public TenLines(ArrayList<T> totalData){
        activeArea = new ArrayList<>();
        passiveArea = (ArrayList<T>) totalData.clone();
        this.totalData = totalData;
        update();
    }


    public ArrayList<T> get_activeArea(){
        return activeArea;
    }

    public ArrayList<T> get_passiveArea(){
        return passiveArea;
    }

    public ArrayList<T> get_totalData(){
        return totalData;
    }

    public int size(){ return totalData.size();}

    /**
     * this should only be called when new data is being loaded in. This completely resets the data that the
     * application is working with to the initial case. This should also only really be needed to be called when the
     * default constructor is used as this is already handled in the other version.
     */
    public void setTotalData(ArrayList<T> totalData){
        this.totalData = totalData;
        passiveArea = (ArrayList<T>) totalData.clone();
        update();
    }

    /**
     * This will take elements from the passiveArea and add it to the activeArea until the activeArea reaches a size of
     * 10. Make this public if this has to be maintained for all data in the textArea and not just for the data loaded
     * in from files.
     */
    private void update(){
        while ((activeArea.size() < 10) && (passiveArea.size() > 0)){
            activeArea.add(passiveArea.remove(0));
        }
    }

}
