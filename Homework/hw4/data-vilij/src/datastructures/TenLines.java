package datastructures;

import java.util.ArrayList;

public class TenLines<T> {
    private ArrayList<T> activeArea = new ArrayList<>();
    private ArrayList<T> passiveArea = new ArrayList<>();

    public static void main(String... args) {
        ArrayList<Integer> test1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            test1.add(i);
        }
        ArrayList<Integer> test2 = new ArrayList<>();
        for (int i = 10; i < 20; i++) {
            test2.add(i);
        }
        ArrayList<Integer> test3 = new ArrayList<>();
        test3.addAll(test1);
        test3.addAll(test2);

        System.out.println(test3.toString());


    }

    public ArrayList<T> get_activeArea() {
        return activeArea;
    }

    public ArrayList<T> get_passiveArea() {
        return passiveArea;
    }

    public ArrayList<T> get_totalData() {
        ArrayList<T> totalData = new ArrayList<>();
        totalData.addAll(activeArea);
        totalData.addAll(passiveArea);
        return totalData;
    }

    /**
     * This will take elements from the passiveArea until it reaches a size of 10.
     */
    public void update_activeArea() {
        int index = 0;
        while (activeArea.size() < 10) {
            activeArea.add(passiveArea.remove(index));
            index++;
        }
    }

    /**
     * All of the data loaded in will initially go into the passiveArea. It is an ArrayList because the order matters
     */
    //TODO Figure out how you want to load the data from the file into this ArrayList. How will load() and TSD prompt work for this
    public void update_passiveArea() {
    }


}
