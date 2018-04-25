package dataprocessors;

import actions.AppActions;
import datastructures.TenLines;
import datastructures.Tuple;
import javafx.beans.property.SimpleBooleanProperty;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent{

    public  TenLines<String>      tenLines;
    private TSDProcessor          processor;
    private ApplicationTemplate   applicationTemplate;
    private String                algorithmsDir;
    private HashSet<String>       labels;
    private SimpleBooleanProperty hasTwoLabels;
    private ArrayList<Integer>    currentAlgorithmConfiguration;

    public AppData(ApplicationTemplate applicationTemplate){
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
        this.tenLines = new TenLines<>();
        this.labels = new HashSet<>();
        this.algorithmsDir = applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHMS_PATH.name());
        this.hasTwoLabels = new SimpleBooleanProperty(false);
        this.currentAlgorithmConfiguration = new ArrayList<>();
    }

    public TenLines<String> getTenLines(){
        return tenLines;
    }

    public HashSet<String> getLabels(){
        return labels;
    }

    public SimpleBooleanProperty hasTwoLabelsProperty(){
        return hasTwoLabels;
    }

    public ArrayList<Integer> getCurrentAlgorithmConfiguration(){
        return currentAlgorithmConfiguration;
    }

    /**
     * This method is meant to load the data into an Array List which then gets put into the tenLines and from there go
     * and project that data on the chart area.
     */
    @Override
    public void loadData(Path dataFilePath){
        int instances = tenLines.size();
        StringBuilder builder = new StringBuilder();
        String filename = dataFilePath.getFileName().toString();
        labels = new HashSet<>();
        tenLines.get_totalData().forEach(point -> {
            String[] pointT = point.split("\t");
            labels.add(pointT[1]);
        });
        tenLines.get_activeArea().forEach(builder::append);
        loadData(builder.toString());
        int numberOfLabels;
        if (labels.contains(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.NULL_STRING.name()))){
            numberOfLabels = labels.size() - 1;
        }
        else{ numberOfLabels = labels.size(); }

        StringBuilder loadedText = new StringBuilder(
                String.format(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.LOADED_DATA.name()),
                              instances, numberOfLabels, filename));

        if (numberOfLabels == 2){
            hasTwoLabels.set(true);
        }
        else{
            hasTwoLabels.set(false);
        }

        ((AppUI) applicationTemplate.getUIComponent()).setDataLoadedIn(true);

        loadedText.append("\n");
        for (Object element : labels)
            loadedText.append(element).append("\n");

        ((AppUI) applicationTemplate.getUIComponent()).setLoadedInFileText(loadedText.toString());
        displayData();
        ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setText(builder.toString());
    }


    public void loadData(String dataString){
        try{
            processor.processString(dataString);
            ArrayList<String> dataArray = new ArrayList<>(Arrays.asList(dataString.split("\n")));
            HashSet<String> labels = new HashSet<>();
            dataArray.forEach(point -> {
                String[] pointT = point.split("\t");
                labels.add(pointT[1]);
            });
            int instances = dataArray.size();
            int numberOfLabels;

            if (labels.contains(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.NULL_STRING.name()))){
                numberOfLabels = labels.size() - 1;
            }
            else{ numberOfLabels = labels.size(); }

            if (numberOfLabels == 2){
                hasTwoLabels.set(true);
            }
            else{
                hasTwoLabels.set(false);
            }

            ((AppUI) applicationTemplate.getUIComponent()).setDataLoadedIn(true);

            StringBuilder loadedText = new StringBuilder(
                    String.format(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.LOADED_DATA.name()),
                                  instances, numberOfLabels,
                                  applicationTemplate.manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name())));
            loadedText.append("\n");
            for (Object element : labels)
                loadedText.append(element).append("\n");

            ((AppUI) applicationTemplate.getUIComponent()).setLoadedInFileText(loadedText.toString());

            ((AppActions) applicationTemplate.getActionComponent()).populateAlgorithms(
                    ((AppUI) applicationTemplate.getUIComponent()).getAlgorithms(), algorithmsDir);

        }
        catch (Exception e){
            ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager = applicationTemplate.manager;
            String errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
            String errMsg = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
            String errInput = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
            dialog.show(errTitle, errMsg + errInput);
        }
    }

    @Override
    public void saveData(Path dataFilePath){
        // NOTE: completing this method was not a part of HW 1. You may have implemented file saving from the
        // confirmation dialog elsewhere in a different way.
        try (PrintWriter writer = new PrintWriter(Files.newOutputStream(dataFilePath))){
            writer.write(((AppUI) applicationTemplate.getUIComponent()).getCurrentText());
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void clear(){
        processor.clear();
    }

    public void displayData(){
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
        ((AppUI) applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
    }

    /**
     * This returns the index of an error if there is. If not, the Tuple.get_key() == -1
     */
    public Tuple<Integer,String> indexOfErrorOrDuplicates(ArrayList<String> arrayList){
        Tuple<Integer,String> tuple = new Tuple<>(-1, "");
        HashMap<String,Integer> map = new HashMap<>();
        arrayList.forEach((String string) -> {
            String[] temp = string.split("\t");
            if (map.containsKey(temp[0])){
                tuple.set_key((arrayList.indexOf(string) + 1));
                tuple.set_value(string);
                tuple.set_isDuplicate(true);
            }
            else{ map.put(temp[0], 1); }
        });

        if (tuple.get_key() != -1) return tuple;

        arrayList.forEach((String string) -> {
            try{
                processor.processString(string);
            }
            catch (Exception e){
                tuple.set_key(arrayList.indexOf(string) + 1);
                tuple.set_value(string);
                tuple.set_isDuplicate(false);
            }

        });

        return tuple;
    }
}
