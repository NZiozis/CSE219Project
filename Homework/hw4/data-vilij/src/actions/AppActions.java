package actions;

import algorithms.Algorithm;
import dataprocessors.AppData;
import datastructures.ConfigurationDialog;
import datastructures.Drop;
import datastructures.Tuple;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static vilij.settings.PropertyTypes.SAVE_WORK_TITLE;
import static vilij.templates.UITemplate.SEPARATOR;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent{

    private List<Integer> output;
    private Drop          drop;
    private Thread        algorithmThread;
    private Algorithm     algorithm;
    private boolean firstIteration = true;
    private XYChart.Series<Number,Number> previousSeries;

    /**
     * Path to the data file currently active.
     */
    private Path                  dataFilePath;
    /**
     * The boolean property marking whether or not there are any unsaved changes.
     */
    private SimpleBooleanProperty isUnsaved;
    /**
     * The application to which this class of actions belongs.
     */
    private ApplicationTemplate   applicationTemplate;


    public AppActions(ApplicationTemplate applicationTemplate){
        this.applicationTemplate = applicationTemplate;
        this.isUnsaved = new SimpleBooleanProperty(false);
        this.drop = new Drop(applicationTemplate);
    }

    public void setIsUnsavedProperty(boolean property){ isUnsaved.set(property); }

    public SimpleBooleanProperty isUnsavedProperty(){
        return isUnsaved;
    }

    public List<Integer> getOutput(){
        return output;
    }

    public void setOutput(List<Integer> output){
        this.output = output;
    }

    @Override
    public void handleNewRequest(){
        ( (AppUI) applicationTemplate.getUIComponent() ).setLoadedInFileText(
                applicationTemplate.manager.getPropertyValue(AppPropertyTypes.NO_DATA_LOADED_IN_PLACEHOLDER.name()));
        ( (AppData) applicationTemplate.getDataComponent() ).hasTwoLabelsProperty().set(false);
        ( (AppUI) applicationTemplate.getUIComponent() ).setDataLoadedIn(false);
        try{
            if (!isUnsaved.get() || promptToSave()){
                applicationTemplate.getDataComponent().clear();
                applicationTemplate.getUIComponent().clear();
                isUnsaved.set(false);
                dataFilePath = null;
                ( (AppUI) applicationTemplate.getUIComponent() ).getEditDoneButton().setDisable(false);
                ( (AppUI) applicationTemplate.getUIComponent() ).getEditDoneButton().setText(
                        applicationTemplate.manager.getPropertyValue(AppPropertyTypes.EDIT_TEXT.name()));
                ( (AppUI) applicationTemplate.getUIComponent() ).getTextArea().setVisible(true);
                ( (AppUI) applicationTemplate.getUIComponent() ).getTextArea().setDisable(true);
            }
        }
        catch (IOException e){ errorHandlingHelper(); }
    }

    @Override
    public void handleSaveRequest(){
        try{
            if (!isUnsaved.get() || promptToSave()){
                isUnsaved.set(false);
            }
        }
        catch (IOException e){ errorHandlingHelper(); }
    }

    @Override
    public void handleLoadRequest(){
        applicationTemplate.getDataComponent().clear();
        applicationTemplate.getUIComponent().clear();
        try{
            if (promptToLoad()){
                ( (AppUI) applicationTemplate.getUIComponent() ).getEditDoneButton().setDisable(true);
                ( (AppUI) applicationTemplate.getUIComponent() ).getTextArea().setVisible(true);
                ( (AppUI) applicationTemplate.getUIComponent() ).getTextArea().setDisable(true);
            }
        }
        catch (IOException e){errorHandlingHelper();}
    }


    @Override
    //TODO edit this so that it takes care of algorithm running when trying to exit case
    public void handleExitRequest(){
        try{
            if (!isUnsaved.get() || promptToSave()) System.exit(0);
        }
        catch (IOException e){ errorHandlingHelper(); }
    }

    public void handleScreenshotRequest(){
        PropertyManager manager = applicationTemplate.manager;
        LineChart<Number,Number> chart = ( (AppUI) applicationTemplate.getUIComponent() ).getChart();
        WritableImage image = chart.snapshot(new SnapshotParameters(), null);

        FileChooser fileChooser = new FileChooser();
        String dataDirPath = SEPARATOR + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
        URL dataDirURL = getClass().getResource(dataDirPath);

        if (dataDirURL == null){
            ErrorDialog dataDirNotFound = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            dataDirNotFound.show(applicationTemplate.manager
                                         .getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()),
                                 applicationTemplate.manager
                                         .getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));
        }

        assert dataDirURL != null;
        fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
        fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));
        String description = manager.getPropertyValue(AppPropertyTypes.SCRNSHT_FILE_DESC.name());
        String extension = manager.getPropertyValue(AppPropertyTypes.SCRNSHT_FILE_EXT.name());
        ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                                                        String.format("*%s", extension));

        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName(manager.getPropertyValue(AppPropertyTypes.SCRNSHT_INITIAL.name()));
        File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

        if (selected != null){
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
            try{
                ImageIO.write(renderedImage,
                              applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SCRNSHT_FILE_EXT.name()),
                              selected);
            }
            catch (IOException e){
                ErrorDialog screenShotError = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                screenShotError.show(applicationTemplate.manager
                                             .getPropertyValue(AppPropertyTypes.SCREENSHOT_ERROR_TITLE.name()),
                                     applicationTemplate.manager
                                             .getPropertyValue(AppPropertyTypes.SCREENSHOT_ERROR_MESSAGE.name()));

            }
        }
    }

    public void handleEditDone(){
        PropertyManager manager = applicationTemplate.manager;
        AppUI ui = ( (AppUI) applicationTemplate.getUIComponent() );

        if (ui.getEditDoneButton().getText().equals(manager.getPropertyValue(AppPropertyTypes.EDIT_TEXT.name()))){
            applicationTemplate.getDataComponent().clear();
            ( (AppUI) applicationTemplate.getUIComponent() ).getChart().getData().clear();
            ui.getEditDoneButton().setText(manager.getPropertyValue(AppPropertyTypes.DONE_TEXT.name()));
            ui.getTextArea().setDisable(false);
        }
        else{
            ui.getEditDoneButton().setText(manager.getPropertyValue(AppPropertyTypes.EDIT_TEXT.name()));
            ui.getTextArea().setDisable(true);
            String data = ( (AppUI) applicationTemplate.getUIComponent() ).getTextArea().getText();
            String[] dataArray = data.split("\n");
            ArrayList<String> arrayListData = new ArrayList<>();
            Collections.addAll(arrayListData, dataArray);
            Tuple<Integer,String> errorTuple =
                    ( (AppData) applicationTemplate.getDataComponent() ).indexOfErrorOrDuplicates(arrayListData);
            if (errorTuple.get_key() == -1){
                ( (AppData) applicationTemplate.getDataComponent() ).loadData(data);
                ( (AppData) applicationTemplate.getDataComponent() ).displayData();
            }
            else{
                ui.setDataLoadedIn(false);
                ui.setLoadedInFileText(applicationTemplate.manager.getPropertyValue(
                        AppPropertyTypes.NO_DATA_LOADED_IN_PLACEHOLDER.name()));
                if (errorTuple.get_isDuplicate()){ duplicateErrorHelper(errorTuple); }
                else{ invalidTextErrorHelper(errorTuple); }
            }
        }

    }


    /**
     * This is here to take the output of the algorithm and put it into mx+b=y form and put it on the chart
     * minX is the minimum x value from the given data set
     * maxX is the maximum x value from the given data set
     * Both are required because they give the base points for out series that will be displayed on the chart
     */
    private void makeline(List<Integer> output){
        int a = output.get(0);
        int b = output.get(1);
        int c = output.get(2);
        ArrayList<String> data = ( (AppData) applicationTemplate.getDataComponent() ).getTenLines().get_totalData();
        LineChart<Number,Number> chart = ( (AppUI) applicationTemplate.getUIComponent() ).getChart();
        ArrayList<Double> xVals = new ArrayList<>();
        data.forEach(line -> xVals.add(Double.parseDouble(line.split("\t")[2].split(",")[0])));

        if (chart.getData().contains(previousSeries)){
            chart.getData().remove(previousSeries);
        }

        double maxX = xVals.get(0);
        double minX = maxX;
        for (double xVal : xVals){
            if (xVal < minX){
                minX = xVal;
            }
            else if (xVal > maxX){
                maxX = xVal;
            }
        }

        double YvalForMinX = formula(minX, a, b, c);
        double YvalForMaxX = formula(maxX, a, b, c);

        //TODO add the proper css for this series so that it is visible as a line
        XYChart.Series<Number,Number> classificationLine = new XYChart.Series<>();
        classificationLine.setName("classificationLine");
        classificationLine.getData().add(new XYChart.Data<>(minX, YvalForMinX));
        classificationLine.getData().add(new XYChart.Data<>(maxX, YvalForMaxX));

        chart.getData().add(0, classificationLine);
        classificationLine.getNode().getStyleClass().add("series-classificationLine");
        classificationLine.getData().forEach(
                element -> element.getNode().getStyleClass().add("series-classificationLine-symbol"));
        previousSeries = classificationLine;
    }

    private double formula(double xVal, int a, int b, int c){

        double xCoefficient = ( (double) -a ) / b;
        double intercept = ( (double) c ) / b;

        return xCoefficient * xVal + intercept;
    }


    private void createAlgorithmThread(Algorithm algorithm){
        algorithmThread = new Thread(algorithm);
        algorithmThread.setName("Algorithm Thread");
    }

    private void createAlgorithmInstance(ArrayList<?> currentConfig, boolean continuousRun){
        String referencePath = ( (AppUI) applicationTemplate.getUIComponent() ).getClassPathtoAlgorithm().toString();

        try{
            Class<?> klass = Class.forName(referencePath);
            Constructor konstructor = klass.getConstructors()[0];

            algorithm = (Algorithm) konstructor
                    .newInstance(null, drop, currentConfig.get(0), currentConfig.get(1), continuousRun);

            // This is how we get the data from the consumer
        }

        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e){
            // This should never occur during the normal use of this version of the program as all classes that are
            // chosen should implement Algorithm which has a default run method, and the files are read directly from
            // the directory they are located in and are not hardcoded. This error could occur if the user deleted an
            // algorithm after the program was started, but this doesn't need to be handled yet.
            e.printStackTrace();
        }

    }

    //TODO when the consumer takes the data what you should do is analyze it and put it in the chart. You should then toggle the buttons appropriately from there
    //Reminder, the config data is now located in the AppData file
    public void handleRunRequest(){

        Platform.runLater(() -> {
            ArrayList<Integer> currentConfig =
                    ( (AppData) applicationTemplate.getDataComponent() ).getCurrentAlgorithmConfiguration();

            boolean continuousRun = currentConfig.get(currentConfig.size() - 1) == 1;

            if (firstIteration){
                createAlgorithmInstance(currentConfig, continuousRun);
                firstIteration = false;
            }

            createAlgorithmThread(algorithm);
            algorithmThread.start();

            output = drop.take();

            if (output == null){
                System.out.println("output is null");
                ( (AppUI) applicationTemplate.getUIComponent() ).setConfigurationValid(false);
                firstIteration = true;
            }
            else{
                if (continuousRun){

                    makeline(output);

                    try{
                        Thread.sleep(800);
                    }
                    catch (InterruptedException ignored){ }

                    //hack way for this to work
                    ( (AppUI) applicationTemplate.getUIComponent() ).getRunButton().fire();
                }

                else{
                    makeline(output);
                }
            }
        });
    }

    /**
     * This is a place holder. I do not intend to implement this, but since we implement ActionComponent it is required.
     */
    public void handlePrintRequest(){}

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException{
        PropertyManager manager = applicationTemplate.manager;
        ConfirmationDialog dialog = ConfirmationDialog.getDialog();
        dialog.show(manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE.name()),
                    manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK.name()));

        if (dialog.getSelectedOption() == null) return false; // if user closes dialog using the window's close button

        if (dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES)){
            if (dataFilePath == null){
                FileChooser fileChooser = new FileChooser();
                String dataDirPath = SEPARATOR + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
                URL dataDirURL = getClass().getResource(dataDirPath);

                if (dataDirURL == null){
                    throw new FileNotFoundException(
                            manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));
                }

                fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
                fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));

                String description = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name());
                String extension = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name());
                ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (*%s)", description, extension),
                                                                String.format("*%s", extension));

                fileChooser.getExtensionFilters().add(extFilter);
                File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
                if (selected != null){
                    dataFilePath = selected.toPath();
                    save();
                }
                else{
                    return false; // if user presses escape after initially selecting 'yes'
                }
            }
            else{ save(); }
        }

        return !dialog.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL);
    }

    private void save(){
        applicationTemplate.getDataComponent().saveData(dataFilePath);
        isUnsaved.set(false);
    }

    /**
     * While similar to promptToSave() it does not offer a confirmation dialog. The prompt to choose a file will
     * appear immediately and that option will load in the data to the tenLines, and from there have the activeArea
     * displayed in the textArea.
     */
    private boolean promptToLoad() throws IOException{
        PropertyManager manager = applicationTemplate.manager;
        ConfirmationDialog dialog = ConfirmationDialog.getDialog();
        dialog.show(manager.getPropertyValue(AppPropertyTypes.LOAD_DATA_TITLE.name()),
                    manager.getPropertyValue(AppPropertyTypes.LOAD_DATA.name()));

        if (dialog.getSelectedOption() == null) return false;

        if (dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES)){

            FileChooser fileChooser = new FileChooser();
            String dataDirPath = SEPARATOR + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
            URL dataDirURL = getClass().getResource(dataDirPath);

            if (dataDirURL == null){
                throw new FileNotFoundException(
                        manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));
            }

            fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
            fileChooser.setTitle(manager.getPropertyValue(AppPropertyTypes.LOAD_DATA_TITLE.name()));

            String description = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name());
            String extension = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name());
            ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                                                            String.format("*%s", extension));

            fileChooser.getExtensionFilters().add(extFilter);
            File selected = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
            if (selected != null){
                ArrayList<String> arrayList = new ArrayList<>();
                Scanner s = new Scanner(new File(selected.toPath().toString())).useDelimiter("\n");
                while (s.hasNextLine()){
                    arrayList.add(s.nextLine() + "\n");
                }

                Tuple<Integer,String> errorTuple =
                        ( (AppData) applicationTemplate.getDataComponent() ).indexOfErrorOrDuplicates(arrayList);

                if (errorTuple.get_key() == -1){
                    dataFilePath = selected.toPath();
                    ( (AppData) applicationTemplate.getDataComponent() ).tenLines.setTotalData(arrayList);
                    load();
                }
                else{
                    dataFilePath = null;
                    if (errorTuple.get_isDuplicate()){ duplicateErrorHelper(errorTuple); }
                    else{ invalidTextErrorHelper(errorTuple); }
                }

            }
            else{
                return false; // if user presses escape after initially selecting 'yes'
            }
        }

        return !dialog.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL);

    }

    private void load(){
        applicationTemplate.getDataComponent().loadData(dataFilePath);
        isUnsaved.set(false);
    }

    /**
     * The types of algorithms will be updated once per the loading off the application. However, the algorithms of
     * that type that are available will be updated everytime that directory is entered. Essentially, ToggleGroup
     * algorithmTypes will be updates once and only once in the following method. However, in populateAlgorithms, the
     * ToggleGroup algorithm will be update everytime the subdirectory containing the actual algorithms is entered.
     */
    public GridPane populateAlgorithms(ToggleGroup algorithms, String algorithmsDirectory){
        GridPane loadedAlgorithms = new GridPane();
        int counter = 0;
        String location = SEPARATOR + algorithmsDirectory;
        URL locationURL = getClass().getResource(location);
        File algorithmsDir = new File(locationURL.getFile());
        ( (AppUI) applicationTemplate.getUIComponent() ).getAlgorithms().getToggles().clear();
        ( (AppUI) applicationTemplate.getUIComponent() ).getSelectButton().setDisable(false);

        String[] directories = algorithmsDir.list((dir, name) -> new File(dir, name).isDirectory());
        if (directories != null && directories.length != 0){
            for (String directory : directories){
                RadioButton radioButton = new RadioButton(directory);
                loadedAlgorithms.add(radioButton, 0, counter++);
                radioButton.setToggleGroup(algorithms);
                if (directory
                        .equals(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name()))){
                    radioButton.disableProperty()
                               .bind(( (AppData) applicationTemplate.getDataComponent() ).hasTwoLabelsProperty().not());
                }
                else{
                    radioButton.setDisable(false);
                }
            }
        }
        else{
            RadioButton back =
                    new RadioButton(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.BACK.name()));
            loadedAlgorithms.add(back, 0, counter++);
            back.setToggleGroup(algorithms);

            String[] algoTypeChecker;
            if (System.getProperty("os.name").toLowerCase().contains("win")){
                algoTypeChecker = algorithmsDir.toString().split(File.separator + File.separator);
            }
            else{
                algoTypeChecker = algorithmsDir.toString().split(SEPARATOR);
            }

            String algoType = algoTypeChecker[algoTypeChecker.length - 1];
            String[] algorithmNames = algorithmsDir.list((file, name) -> new File(file, name).isFile());
            assert algorithmNames != null;
            if (algoType.equals(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name()))){

                for (String algoName : algorithmNames){
                    RadioButton radioButton = new RadioButton(algoName);

                    loadedAlgorithms.add(radioButton, 0, counter);
                    Button configurationButton = createNewConfigurationButton(algoName, true, counter);
                    loadedAlgorithms.add(configurationButton, 1, counter++);
                    radioButton.setToggleGroup(algorithms);
                }
            }
            else{
                for (String algoName : algorithmNames){
                    RadioButton radioButton = new RadioButton(algoName);

                    loadedAlgorithms.add(radioButton, 0, counter);
                    Button configurationButton = createNewConfigurationButton(algoName, false, counter);
                    loadedAlgorithms.add(configurationButton, 1, counter++);
                    radioButton.setToggleGroup(algorithms);
                }
            }
        }
        return loadedAlgorithms;
    }

    private Button createNewConfigurationButton(String algoName, boolean isClustering, Integer counter){
        Button configurationButton = new Button(null, new ImageView(new Image(
                applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CONFIGURATION_ICON_PATH.name()))));
        Tooltip tooltip = new Tooltip(
                algoName + applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CONFIGURATION_TOOLTIP.name()));
        configurationButton.setTooltip(tooltip);
        configurationButton.getStyleClass().add("configuration-button");

        ConfigurationDialog dialog = new ConfigurationDialog(applicationTemplate, isClustering, counter);
        dialog.init(applicationTemplate.getUIComponent().getPrimaryWindow());
        configurationButton.setOnMouseClicked(event -> dialog
                .show(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CONFIGURATION_TITLE.name()), null));

        return configurationButton;

    }

    private void errorHandlingHelper(){
        ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager = applicationTemplate.manager;
        String errTitle = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name());
        String errMsg = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
        String errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
        dialog.show(errTitle, errMsg + errInput);
    }

    private void invalidTextErrorHelper(Tuple<Integer,String> tuple){
        ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager = applicationTemplate.manager;
        String errTitle = manager.getPropertyValue(AppPropertyTypes.INVALID_TEXT_ERROR_TITLE.name());
        String errMsg = manager.getPropertyValue(AppPropertyTypes.INVALID_TEXT_ERROR_MESSAGE.name());
        String invalidElement = tuple.get_value();
        String invalidIndex = tuple.get_key().toString();

        dialog.show(errTitle, String.format(errMsg, invalidIndex, invalidElement));
    }

    private void duplicateErrorHelper(Tuple<Integer,String> tuple){
        ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager = applicationTemplate.manager;
        String errTitle = manager.getPropertyValue(AppPropertyTypes.DUPLICATE_ERROR_TITLE.name());
        String errMsg = manager.getPropertyValue(AppPropertyTypes.DUPLICATE_ERROR_MESSAGE.name());
        String duplicateElement = tuple.get_value();
        String duplicateIndex = tuple.get_key().toString();

        dialog.show(errTitle, String.format(errMsg, duplicateIndex, duplicateElement));
    }
}
