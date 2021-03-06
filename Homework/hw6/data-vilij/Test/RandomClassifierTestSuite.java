import dataprocessors.AppData;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


/**
 * Inside of the init method, each unique configuration dialog gets its own button that has a lambda that tells it
 * what do to when pressed. There are three elements that are updated:
 * {@link datastructures.ConfigurationDialog#maxIterations}
 * {@link datastructures.ConfigurationDialog#updateInterval}
 * {@link datastructures.ConfigurationDialog#continuousRun}
 * Since these are all TextFields, getText() is called on them. Then, the first two are validated to make sure that
 * they are greater than 1, and that the updateInterval, when converted to an Integer isn't greater than
 * maxIterations. This is why 1 is a boundary value for both of these inputs. continuousRun is a CheckBox, and if it
 * is true it stores a 1 in {@link AppData#currentAlgorithmConfiguration} and a 0 when false.
 *
 * @author Niko Ziozis
 */


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

    private ArrayList<Integer> getInputs(String maxIterations, String updateInterval, boolean isContinuos){
        ArrayList<Integer> arrayList = new ArrayList<>();

        Integer maxIterationsInt = Integer.parseInt(invalidInputHandler(maxIterations));
        Integer updateIntervalInt = Integer.parseInt(invalidInputHandler(updateInterval));

        arrayList.add(maxIterationsInt);
        if (updateIntervalInt > maxIterationsInt){ arrayList.add(maxIterationsInt); }
        else{ arrayList.add(updateIntervalInt); }

        if (isContinuos){ arrayList.add(1); }
        else{ arrayList.add(0); }

        return arrayList;
    }

    @Test
    public void validConfiguration(){
        ArrayList<Integer> inputs = getInputs("3", "1", false);

        assertEquals((Integer) 3, inputs.get(0));
        assertEquals((Integer) 1, inputs.get(1));
        assertEquals((Integer) 0, inputs.get(2));
    }

    @Test
    public void updateIntervalIsGreaterThanMaxIterations(){ // in this case, the update interval should be set to maxInterval
        ArrayList<Integer> inputs = getInputs("2", "2312", false);

        assertEquals((Integer) 2, inputs.get(1));
    }


    @Test
    public void boundaryCaseMaxIterationsOne(){
        ArrayList<Integer> inputs = getInputs("1", "20", true);

        assertEquals((Integer) 1, inputs.get(0));
    }

    @Test
    public void boundaryCaseUpdateIntervalOne(){
        ArrayList<Integer> inputs = getInputs("20", "1", true);

        assertEquals((Integer) 1, inputs.get(1));
    }

    @Test
    public void invalidCaseMaxIterationsNegativeOrZero(){
        ArrayList<Integer> negative = getInputs("-5", "20", false);
        ArrayList<Integer> zero = getInputs("0", "20", false);

        assertEquals((Integer) 1, negative.get(0));
        assertEquals((Integer) 1, zero.get(0));
    }

    @Test
    public void invalidCaseUpdateIntervalNegativeOrZero(){
        ArrayList<Integer> negative = getInputs("20", "-4", false);
        ArrayList<Integer> zero = getInputs("20", "0", true);

        assertEquals((Integer) 1, negative.get(1));
        assertEquals((Integer) 1, zero.get(1));
    }

    @Test
    public void boundaryCaseIsContinuous(){
        ArrayList<Integer> inputs = getInputs("20", "5", true);

        assertEquals((Integer) 1, inputs.get(2));

    }

    @Test
    public void boundaryCaseIsNotContinuous(){
        ArrayList<Integer> inputs = getInputs("20", "5", false);

        assertEquals((Integer) 0, inputs.get(2));
    }

}
