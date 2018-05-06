import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class RandomClustererTestSuite{

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

    @SuppressWarnings("Duplicates")
    private ArrayList<Integer> getInputs(String maxIterations, String updateInterval, String numberOfLabelsChosen,
                                         boolean isContinuos){
        ArrayList<Integer> arrayList = new ArrayList<>();

        Integer maxIterationsInt = Integer.parseInt(invalidInputHandler(maxIterations));
        Integer updateIntervalInt = Integer.parseInt(invalidInputHandler(updateInterval));
        Integer numberOfLabels = Integer.parseInt(invalidInputHandler(numberOfLabelsChosen));

        arrayList.add(maxIterationsInt);
        if (updateIntervalInt > maxIterationsInt){ arrayList.add(maxIterationsInt); }
        else{ arrayList.add(updateIntervalInt); }

        if (numberOfLabels == 1){ arrayList.add(2);}
        else if (numberOfLabels > 4){ arrayList.add(4); }
        else{ arrayList.add(numberOfLabels); }

        if (isContinuos){ arrayList.add(1); }
        else{ arrayList.add(0); }

        return arrayList;
    }

    @Test
    public void validConfiguration(){
        ArrayList<Integer> inputs = getInputs("4", "1", "2", false);

        assertEquals((Integer) 4, inputs.get(0));
        assertEquals((Integer) 1, inputs.get(1));
        assertEquals((Integer) 2, inputs.get(2));
        assertEquals((Integer) 0, inputs.get(3));
    }

    @Test
    public void updateIntervalIsGreaterThanMaxIterations(){ // in this case, the update interval should be set to maxInterval
        ArrayList<Integer> inputs = getInputs("2", "2312", "2", false);

        assertEquals((Integer) 2, inputs.get(1));
    }

    @Test
    public void boundaryCaseMaxIterationsOne(){
        ArrayList<Integer> inputs = getInputs("1", "20", "3", true);

        assertEquals((Integer) 1, inputs.get(0));
    }

    @Test
    public void boundaryCaseUpdateIntervalOne(){
        ArrayList<Integer> inputs = getInputs("20", "1", "2", true);

        assertEquals((Integer) 1, inputs.get(1));
    }

    @Test
    public void invalidCaseMaxIterationsNegativeOrZero(){
        ArrayList<Integer> negative = getInputs("-5", "20", "4", false);
        ArrayList<Integer> zero = getInputs("0", "20", "2", false);

        assertEquals((Integer) 1, negative.get(0));
        assertEquals((Integer) 1, zero.get(0));
    }

    @Test
    public void invalidCaseUpdateIntervalNegativeOrZero(){
        ArrayList<Integer> negative = getInputs("20", "-4", "2", false);
        ArrayList<Integer> zero = getInputs("20", "0", "3", true);

        assertEquals((Integer) 1, negative.get(1));
        assertEquals((Integer) 1, zero.get(1));
    }

    @Test
    public void boundaryCaseIsContinuous(){
        ArrayList<Integer> inputs = getInputs("20", "5", "2", true);

        assertEquals((Integer) 1, inputs.get(3));

    }

    @Test
    public void boundaryCaseIsNotContinuous(){
        ArrayList<Integer> inputs = getInputs("20", "5", "4", false);

        assertEquals((Integer) 0, inputs.get(3));
    }

    @Test
    public void boundaryCaseNumberOfLabelsChosenTwo(){
        ArrayList<Integer> inputs = getInputs("20", "5", "2", true);

        assertEquals((Integer) 2, inputs.get(2));
    }

    @Test
    public void boundaryCaseNumberOfLabelsChosenFour(){
        ArrayList<Integer> inputs = getInputs("20", "5", "4", true);

        assertEquals((Integer) 4, inputs.get(2));
    }

    @Test
    public void invalidInputNUmberOfLabelsChosenLessThanTwo(){
        ArrayList<Integer> inputs = getInputs("22", "5", "0", true);
        ArrayList<Integer> inputs1 = getInputs("22", "5", "-4", true);
        ArrayList<Integer> inputs2 = getInputs("22", "5", "1", true);

        assertEquals((Integer) 2, inputs.get(2));
        assertEquals((Integer) 2, inputs1.get(2));
        assertEquals((Integer) 2, inputs2.get(2));
    }

    @Test
    public void invalidInputNumberOfLabelsChosenGreaterThanFour(){
        ArrayList<Integer> inputs = getInputs("20", "5", "8", true);

        assertEquals((Integer) 4, inputs.get(2));
    }

}
