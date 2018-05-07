import dataprocessors.TSDProcessor;
import javafx.geometry.Point2D;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class TSDTestSuite{

    /**
     * <p>
     * The boundary values for this is when either the name or the label are one character long. The String "null"
     * will also be included here as it is handled differently than most other labels. The Point2D location can be
     * any Double and so isn't restricted by a particular boundary value. An invalid name is one that doesn't have an @
     * symbol in front of it.
     *
     * @author Niko Ziozis
     */

    private Map<String,String>  dataLabels;
    private Map<String,Point2D> dataPoints;

    private String checkedname(String name) throws TSDProcessor.InvalidDataNameException{
        if (!name.startsWith("@")) throw new TSDProcessor.InvalidDataNameException(name);
        return name;
    }

    private String checkedLabel(String label) throws TSDProcessor.InvalidLabelException{
        if (label == null || label.length() <= 0) throw new TSDProcessor.InvalidLabelException(label);
        return label;
    }

    @SuppressWarnings("Duplicates")
    private void processString(String tsdString) throws TSDProcessor.InvalidDataNameException,
                                                        TSDProcessor.InvalidLabelException{
        AtomicBoolean hadAnError = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n")).map(line -> Arrays.asList(line.split("\t"))).forEach(list -> {
            try{
                String name = checkedname(list.get(0));
                String label = checkedLabel(list.get(1));
                String[] pair = list.get(2).split(",");
                Point2D point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                dataLabels.put(name, label);
                dataPoints.put(name, point);
            }
            catch (Exception e){
                errorMessage.setLength(0);
                errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                hadAnError.set(true);
            }
        });
        if (errorMessage.length() > 0){
            if (errorMessage.indexOf("Name") != -1){
                throw new TSDProcessor.InvalidDataNameException(errorMessage.toString());
            }
            else{ throw new TSDProcessor.InvalidLabelException(errorMessage.toString()); }
        }
    }

    @Test(expected = TSDProcessor.InvalidLabelException.class)
    public void invalidTSDLabel() throws Exception{
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        String invalidLabel = "@asd\t\t12,3\n";
        processString(invalidLabel);
    }

    @Test(expected = TSDProcessor.InvalidDataNameException.class)
    public void invalidTSDInputName() throws Exception{
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        String invalidName = "asdf\tadfgre\t1,2\n";
        processString(invalidName);
    }

    @Test
    public void validTSDinput() throws TSDProcessor.InvalidDataNameException, TSDProcessor.InvalidLabelException{
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        String validString = "@name\tlabel\t2,2\n";
        Map<String,String> tempDataLabels = new HashMap<>();
        Map<String,Point2D> tempDataPoints = new HashMap<>();

        processString(validString);
        tempDataLabels.put("@name", "label");
        tempDataPoints.put("@name", new Point2D(2, 2));

        assertEquals(tempDataLabels, dataLabels);
        assertEquals(tempDataPoints, dataPoints);

    }

    @Test
    public void boundaryCaseSingleCharName() throws TSDProcessor.InvalidDataNameException,
                                                    TSDProcessor.InvalidLabelException{
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        String boundCaseSingleCharName = "@n\tlabel\t2,1\n"; // This makes sure that it can handle a name of length one
        Map<String,String> tempDataLabels = new HashMap<>();
        Map<String,Point2D> tempDataPoints = new HashMap<>();

        processString(boundCaseSingleCharName);
        tempDataLabels.put("@n", "label");
        tempDataPoints.put("@n", new Point2D(2, 1));

        assertEquals(tempDataLabels, dataLabels);
        assertEquals(tempDataPoints, dataPoints);
    }

    @Test
    public void boundaryCaseSingleCharLabel() throws Exception{
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        String boundaryCaseSingleCharLabel = "@name\tl\t12,43\n"; // makes sure that it can handle a label of length one
        Map<String,String> tempDataLabels = new HashMap<>();
        Map<String,Point2D> tempDataPoints = new HashMap<>();

        processString(boundaryCaseSingleCharLabel);
        tempDataLabels.put("@name", "l");
        tempDataPoints.put("@name", new Point2D(12, 43));

        assertEquals(tempDataLabels, dataLabels);
        assertEquals(tempDataPoints, dataPoints);
    }

    @Test
    public void boundaryCaseNullStringLabel() throws Exception{
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        String boundaryCaseNullStringLabel = "@name\tnull\t12,43\n"; // makes sure that it can handle a label of "null"
        Map<String,String> tempDataLabels = new HashMap<>();
        Map<String,Point2D> tempDataPoints = new HashMap<>();

        processString(boundaryCaseNullStringLabel);
        tempDataLabels.put("@name", "null");
        tempDataPoints.put("@name", new Point2D(12, 43));

        assertEquals(tempDataLabels, dataLabels);
        assertEquals(tempDataPoints, dataPoints);
    }

}
