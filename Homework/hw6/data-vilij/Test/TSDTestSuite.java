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

    private Map<String,String>  dataLabels;
    private Map<String,Point2D> dataPoints;

    private String checkedname(String name) throws TSDProcessor.InvalidDataNameException{
        if (!name.startsWith("@")) throw new TSDProcessor.InvalidDataNameException(name);
        return name;
    }

    @SuppressWarnings("Duplicates")
    private void processString(String tsdString) throws TSDProcessor.InvalidDataNameException{
        AtomicBoolean hadAnError = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n")).map(line -> Arrays.asList(line.split("\t"))).forEach(list -> {
            try{
                String name = checkedname(list.get(0));
                String label = list.get(1);
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
        if (errorMessage.length() > 0) throw new TSDProcessor.InvalidDataNameException(errorMessage.toString());
    }


    @Test(expected = TSDProcessor.InvalidDataNameException.class)
    public void invalidTSDinput() throws Exception{
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        String invalidString = "asdf\tadfgre\t1,2\n";
        processString(invalidString);
    }

    @Test
    public void validTSDinput() throws TSDProcessor.InvalidDataNameException{
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
    public void boundaryCaseSingleCharName() throws TSDProcessor.InvalidDataNameException{
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
