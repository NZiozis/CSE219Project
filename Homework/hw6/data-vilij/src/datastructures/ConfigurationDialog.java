package datastructures;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.Dialog;
import vilij.templates.ApplicationTemplate;

import java.util.ArrayList;

public class ConfigurationDialog extends Stage implements Dialog{

    private ApplicationTemplate applicationTemplate;
    private TextField           maxIterations;
    private TextField           updateInterval;
    private CheckBox            continuousRun;
    private boolean             isClustering;
    private ComboBox<String>    numLables;
    private String              numberOfLabelsChosen;
    private Integer             row;

    public ConfigurationDialog(ApplicationTemplate applicationTemplate, boolean isClustering, Integer row){
        this.applicationTemplate = applicationTemplate;
        this.isClustering = isClustering;
        this.row = row;
    }

    @Override
    public void show(String dialogTitle, String message){
        setTitle(dialogTitle);           // set the title of the dialog
        showAndWait();                   // open the dialog and wait for the user to click the close button
    }

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


    /**
     * Inside of the init method, each unique configuration dialog gets its own button that has a lambda that tells it
     * what do to when pressed. If the algorithm is Classificaiton, there are three elements that are updated:
     * {@link ConfigurationDialog#maxIterations}
     * {@link ConfigurationDialog#updateInterval}
     * {@link ConfigurationDialog#continuousRun}
     * Since these are all TextFields, getText() is called on them. Then, the first two are validated to make sure that
     * they are greater than 1, and that the updateInterval, when converted to an Integer isn't greater than
     * maxIterations. This is why 1 is a boundary value for both of these inputs. continuousRun is a CheckBox, and if it
     * is true it stores a 1 in {@link AppData#currentAlgorithmConfiguration} and a 0 when false.
     * If the algorithm is Clustering, in addition to the above elements there is a also a ComboBox that stores the
     * number of Clusters available to choose from {@link ConfigurationDialog#numLables}. The ComboBox is populated with
     * numbers from 2 to 4 inclusive. As such, the boundary values are 2 on the low end and 4 on the high end. If this
     * were taken as an unrestricted user input (ie TextField) the value obtained would be verified to be inside of the
     * aforementioned range. When the data stored in numLabels is exported to the rest of the application, it is at
     * index 2, in between the value from updateInterval and the value from continuousRun.
     *
     * @author Niko Ziozis
     */
    @Override
    public void init(Stage owner){

        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);

        Button confirmationButton = new Button(Option.CONFIRM.name());
        int rowIndex = 0;

        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().add(confirmationButton);

        GridPane settingsPane = new GridPane();
        settingsPane.setHgap(10);
        settingsPane.setVgap(10);
        settingsPane.setPadding(new Insets(10, 10, 10, 10));

        settingsPane.add(new Text(
                                 applicationTemplate.manager.getPropertyValue(AppPropertyTypes.MAX_ITERATIONS_TEXT.name())), 0,
                         rowIndex);
        maxIterations = new TextField();
        maxIterations.setMaxSize(100, 30);
        maxIterations.setMinSize(100, 30);
        settingsPane.add(maxIterations, 1, rowIndex++);

        settingsPane.add(new Text(
                                 applicationTemplate.manager.getPropertyValue(AppPropertyTypes.UPDATE_INTERVAL_TEXT.name())), 0,
                         rowIndex);
        updateInterval = new TextField();
        updateInterval.setMaxSize(100, 30);
        updateInterval.setMinSize(100, 30);
        settingsPane.add(updateInterval, 1, rowIndex++);

        if (isClustering){
            settingsPane
                    .add(new Text(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.LABELS_NUM.name())), 0,
                         rowIndex);
            numLables = new ComboBox<>();
            for (int i = 1; i < ( (AppData) applicationTemplate.getDataComponent() ).getLabels().size() && i < 4; i++){
                numLables.getItems().add(i + 1 + "");
            }
            settingsPane.add(numLables, 1, rowIndex++);
        }

        settingsPane.add(new Text(
                                 applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CONTINUOUS_RUN_TEXT.name())), 0,
                         rowIndex);
        continuousRun = new CheckBox();
        settingsPane.add(continuousRun, 1, rowIndex);

        VBox messagePane = new VBox(settingsPane, buttonBox);
        messagePane.setAlignment(Pos.CENTER);
        messagePane.setPadding(new Insets(10, 20, 20, 20));
        messagePane.setSpacing(10);

        confirmationButton.setOnMouseClicked(event -> {

            ( (AppActions) applicationTemplate.getActionComponent() ).setFirstIteration(true);

            maxIterations.setText(invalidInputHandler(maxIterations.getText()));
            updateInterval.setText(invalidInputHandler(updateInterval.getText()));

            if (Integer.parseInt(updateInterval.getText()) > Integer.parseInt(maxIterations.getText())){
                updateInterval.setText(maxIterations.getText());
            }

            if (isClustering){
                numberOfLabelsChosen = invalidInputHandler(numLables.getValue());
                if (Integer.parseInt(numberOfLabelsChosen) == 1) numberOfLabelsChosen = "2";
                numLables.setValue(numberOfLabelsChosen);
            }

            continuousRun.setSelected(continuousRun.isSelected());
            if (this.row.equals(( (AppUI) applicationTemplate.getUIComponent() ).getCurrentAlgoIndex())){
                ( (AppUI) applicationTemplate.getUIComponent() ).setConfigurationValid(true);
            }

            ArrayList<Integer> externalConfig =
                    ( (AppData) applicationTemplate.getDataComponent() ).getCurrentAlgorithmConfiguration();

            externalConfig.clear();

            externalConfig.add(Integer.parseInt(maxIterations.getText()));
            externalConfig.add(Integer.parseInt(updateInterval.getText()));
            if (isClustering) externalConfig.add(Integer.parseInt(numLables.getValue()));

            /*Here, 1 means that it is selected and 0 means that it isn't*/
            if (continuousRun.isSelected()){ externalConfig.add(1); }
            else{ externalConfig.add(0); }

            this.close();
        });

        this.setScene(new Scene(messagePane));
    }

    @SuppressWarnings("unused")
    public CheckBox getContinuousRun(){
        return continuousRun;
    }

    @SuppressWarnings("unused")
    public TextField getMaxIterations(){
        return maxIterations;
    }

    @SuppressWarnings("unused")
    public TextField getUpdateInterval(){
        return updateInterval;
    }

    public enum Option{
        CONFIRM("Confirm");


        @SuppressWarnings("unused")
        private String option;

        Option(String option){this.option = option;}
    }
}
