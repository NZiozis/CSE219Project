import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


public class RandomClassifierTestSuite{

    @SuppressWarnings("Duplicates")
    private String invalidInputHandler(String string){
        try{
            int fault = Integer.parseInt(string);
            if (fault < 1){
                throw new Exception();
            }
            return string;
        }
        catch (Exception e){
            return "1";
        }
    }

    private ArrayList<Integer> getInputs(String maxIterations, String updateInterval){
        ArrayList<Integer> arrayList = new ArrayList<>();

        Integer maxIterationsInt = Integer.parseInt(invalidInputHandler(maxIterations));
        Integer updateIntervalInt = Integer.parseInt(invalidInputHandler(updateInterval));

        arrayList.add(maxIterationsInt);
        if (updateIntervalInt > maxIterationsInt){ arrayList.add(maxIterationsInt); }
        else{ arrayList.add(updateIntervalInt); }

        return arrayList;
    }

    @Test
    public void validConfiguration(){
        ArrayList<Integer> inputs = getInputs("3", "1");
        assertEquals((Integer) 3, inputs.get(0));
        assertEquals((Integer) 1, inputs.get(1));
    }

    @Test
    public void updateIntervalIsGreaterThanMaxIterations(){ // in this case, the update interval should be set to maxInterval
        ArrayList<Integer> inputs = getInputs("2", "2312");
        assertEquals((Integer) 2, inputs.get(1));
    }


    @Test
    public void boundaryCaseMaxIterationsOne(){
        ArrayList<Integer> inputs = getInputs("1", "20");
        assertEquals((Integer) 1, inputs.get(0));
    }

    @Test
    public void boundaryCaseUpdateIntervalOne(){
        ArrayList<Integer> inputs = getInputs("20", "1");
        assertEquals((Integer) 1, inputs.get(1));
    }

    @Test
    public void invalidCaseMaxIterationsNegativeOrZero(){
        ArrayList<Integer> negative = getInputs("-5", "20");
        ArrayList<Integer> zero = getInputs("0", "20");

        assertEquals((Integer) 1, negative.get(0));
        assertEquals((Integer) 1, zero.get(0));
    }

    @Test
    public void invalidCaseUpdateIntervalNegativeOrZero(){
        ArrayList<Integer> negative = getInputs("20", "-4");
        ArrayList<Integer> zero = getInputs("20", "0");

        assertEquals((Integer) 1, negative.get(1));
        assertEquals((Integer) 1, zero.get(1));
    }


}
