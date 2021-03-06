package dataprocessors;

import algorithms.DataSet;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.stage.PopupWindow;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor{

    private Map<String,String>  dataLabels;
    private Map<String,Point2D> dataPoints;

    TSDProcessor(){
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     * <p>
     * The boundary values for this is when either the name or the label are one character long. The String "null"
     * will also be included here as it is handled differently than most other labels. The Point2D location can be
     * any Double and so isn't restricted by a particular boundary value.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format.
     *                   The exception will be thrown especially when the @ symbol doesn't prepend the name to
     *                   a given Instance. There will also be an error if one of the inputs is missing.
     * @author Niko Ziozis
     */
    public void processString(String tsdString) throws Exception{
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
        if (errorMessage.length() > 0) throw new Exception(errorMessage.toString());
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    public void toChartData(XYChart<Number,Number> chart){
        Set<String> labels = new HashSet<>(dataLabels.values());
        for (String label : labels){
            XYChart.Series<Number,Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY(), entry.getKey().substring(1)));
            });
            chart.getData().add(series);
        }
        addTooltips(chart);
    }

    void clear(){
        dataPoints.clear();
        dataLabels.clear();
    }

    @SuppressWarnings("unchecked")
        //seems silly to have to suppress this, but Intellij won't have it any other way.
    void putNewDataSetToChart(DataSet output){

        dataPoints = (HashMap<String,Point2D>) ( (HashMap<String,Point2D>) output.getLocations() ).clone();
        dataLabels = (HashMap<String,String>) ( (HashMap<String,String>) output.getLabels() ).clone();
    }

    private String checkedname(String name) throws InvalidDataNameException{
        if (!name.startsWith("@")) throw new InvalidDataNameException(name);
        return name;
    }

    private String checkedLabel(String label) throws InvalidLabelException{
        if (label == null || label.length() <= 0) throw new InvalidLabelException(label);
        return label;
    }

    private void addTooltips(XYChart<Number,Number> chart){
        for (int i = 0; i < chart.getData().size(); i++){
            for (XYChart.Data<Number,Number> data : chart.getData().get(i).getData()){
                Node node = data.getNode();
                node.setCursor(Cursor.HAND);
                Tooltip t;
                if (data.getExtraValue() == null){
                    t = new Tooltip("null");
                }
                else{
                    t = new Tooltip(data.getExtraValue().toString());
                }
                t.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_BOTTOM_RIGHT);
                Tooltip.install(node, t);
            }
        }
    }

    public static class InvalidDataNameException extends Exception{

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name){
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    public static class InvalidLabelException extends Exception{
        private static String LABEL_ERROR_MSG = "All labels must have at least a length of 1.";

        public InvalidLabelException(String label){
            super(String.format("Invalid label '%s'." + LABEL_ERROR_MSG, label));
        }
    }
}
