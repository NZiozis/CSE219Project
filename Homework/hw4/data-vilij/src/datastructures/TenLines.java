package datastructures;

import java.util.ArrayList;

public class TenLines<T> {
    private ArrayList<T> activeArea;
    private ArrayList<T> passiveArea;
    private ArrayList<T> totalData;


    public TenLines() {
        activeArea = new ArrayList<>();
        passiveArea = new ArrayList<>();
        totalData = new ArrayList<>();
    }

    public TenLines(ArrayList<T> totalData) {
        activeArea = new ArrayList<>();
        passiveArea = totalData;
        this.totalData = totalData;
        update_activeArea();
    }


    public ArrayList<T> get_activeArea() {
        return activeArea;
    }

    public ArrayList<T> get_passiveArea() {
        return passiveArea;
    }

    public ArrayList<T> get_totalData() {
        return totalData;
    }

    /**
     * this should only be called when new data is being loaded in. This completely resets the data that the
     * application is working with to the initial case. This should also only really be needed to be called when the
     * default constructor is used as this is already handled in the other version.
     */
    public void setTotalData(ArrayList<T> totalData) {
        this.totalData = totalData;
        passiveArea = totalData;
        update_activeArea();
    }


    /**
     * This will take elements from the passiveArea until it reaches a size of 10.
     */
    private void update_activeArea() {
        int index = 0;
        while (activeArea.size() < 10 || passiveArea.size() == 0) {
            activeArea.add(passiveArea.remove(index));
            index++;
        }
    }

    /**
     * All of the data loaded in will initially go into the passiveArea. It is an ArrayList because the order matters
     */
    //TODO Figure out how you want to load the data from the file into this ArrayList. How will load() and TSD prompt work for this
    public void update_passiveArea(T data) {
        passiveArea.add(data);
    }

}
