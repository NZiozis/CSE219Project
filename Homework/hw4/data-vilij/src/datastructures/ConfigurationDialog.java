package datastructures;

import dataprocessors.AppData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.components.Dialog;
import vilij.templates.ApplicationTemplate;

public class ConfigurationDialog extends Stage implements Dialog{

    private ApplicationTemplate applicationTemplate;
    private TextArea            maxIterations;
    private TextArea            updateInterval;
    private CheckBox            continuousRun;
    private boolean             isClustering;
    private ComboBox            numLables;
    private String              numberOfLabelsChosen;

    public ConfigurationDialog(ApplicationTemplate applicationTemplate, boolean isClustering){
        this.applicationTemplate = applicationTemplate;
        this.isClustering = isClustering;
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

    @Override
    public void init(Stage owner){

        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);

        Button confirmationButton = new Button(Option.CONFIRM.name());
        confirmationButton.setOnMouseClicked(event -> {


            String graceful  = invalidInputHandler(maxIterations.getText());
            maxIterations.setText(graceful);
            String degradation = invalidInputHandler(updateInterval.getText());
            updateInterval.setText(degradation);

            if (isClustering){
                numberOfLabelsChosen = invalidInputHandler((String)numLables.getValue());
                numLables.setValue(numberOfLabelsChosen);
            }

            continuousRun.setSelected(continuousRun.isSelected());
            this.close();
        });
        int rowIndex = 0;

        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().add(confirmationButton);

        GridPane settingsPane = new GridPane();
        settingsPane.setHgap(10);
        settingsPane.setVgap(10);
        settingsPane.setPadding(new Insets(10, 10, 10, 10));

        settingsPane.add(
                new Text(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.MAX_ITERATIONS_TEXT.name())), 0,
                rowIndex);
        maxIterations = new TextArea();
        maxIterations.setMaxSize(100, 30);
        maxIterations.setMinSize(100, 30);
        settingsPane.add(maxIterations, 1, rowIndex++);

        settingsPane.add(
                new Text(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.UPDATE_INTERVAL_TEXT.name())), 0,
                rowIndex);
        updateInterval = new TextArea();
        updateInterval.setMaxSize(100, 30);
        updateInterval.setMinSize(100, 30);
        settingsPane.add(updateInterval, 1, rowIndex++);

        if (isClustering){
            settingsPane.add(new Text(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.LABELS_NUM.name())),
                             0, rowIndex);
            numLables = new ComboBox();
            for (int i = 0; i < ((AppData) applicationTemplate.getDataComponent()).getLabels().size(); i++){
                numLables.getItems().add(i + 1 + "");
            }
            settingsPane.add(numLables, 1, rowIndex++);
        }


        settingsPane.add(
                new Text(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CONTINUOUS_RUN_TEXT.name())), 0,
                rowIndex);
        continuousRun = new CheckBox();
        settingsPane.add(continuousRun, 1, rowIndex++);


        VBox messagePane = new VBox(settingsPane, buttonBox);
        messagePane.setAlignment(Pos.CENTER);
        messagePane.setPadding(new Insets(10, 20, 20, 20));
        messagePane.setSpacing(10);

        this.setScene(new Scene(messagePane));
    }

    @SuppressWarnings("unused")
    public CheckBox getContinuousRun(){
        return continuousRun;
    }

    @SuppressWarnings("unused")
    public TextArea getMaxIterations(){
        return maxIterations;
    }

    @SuppressWarnings("unused")
    public TextArea getUpdateInterval(){
        return updateInterval;
    }

    public enum Option{
        CONFIRM("Confirm");


        @SuppressWarnings("unused")
        private String option;

        Option(String option){this.option = option;}
    }
}
