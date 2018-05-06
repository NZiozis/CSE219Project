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
 * Since the algorithm is Clustering, in addition to the above elements there is a also a ComboBox that stores the
 * number of Clusters available to choose from {@link datastructures.ConfigurationDialog#numLables}. The ComboBox is populated with
 * numbers from 2 to 4 inclusive. As such, the boundary values are 2 on the low end and 4 on the high end. If this
 * were taken as an unrestricted user input (ie TextField) the value obtained would be verified to be inside of the
 * aforementioned range. When the data stored in numLabels is exported to the rest of the application, it is at
 * index 2, in between the value from updateInterval and the value from continuousRun.
 *
 * @author Niko Ziozis
 */


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
