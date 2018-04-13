package datastructures;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
    private CheckBox continuousRun;

    public ConfigurationDialog(ApplicationTemplate applicationTemplate){
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void show(String dialogTitle, String message){
        setTitle(dialogTitle);           // set the title of the dialog

        showAndWait();                   // open the dialog and wait for the user to click the close button
    }

    @Override
    public void init(Stage owner){

        initModality(Modality.WINDOW_MODAL); // modal => messages are blocked from reaching other windows
        initOwner(owner);

        Button confirmationButton = new Button(Option.CONFIRM.name());
        confirmationButton.setOnMouseClicked(event -> {
            maxIterations.setText(maxIterations.getText());
            updateInterval.setText(updateInterval.getText());
            continuousRun.setSelected(continuousRun.isSelected());
            this.close();
        });

        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().add(confirmationButton);

        GridPane settingsPane = new GridPane();
        settingsPane.setHgap(10);
        settingsPane.setVgap(10);
        settingsPane.setPadding(new Insets(10, 10, 10, 10));

        settingsPane.add(
                new Text(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.MAX_ITERATIONS_TEXT.name())), 0,
                0);
        maxIterations = new TextArea();
        maxIterations.setMaxSize(100,30);
        maxIterations.setMinSize(100,30);
        settingsPane.add(maxIterations, 1, 0);

        settingsPane.add(
                new Text(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.UPDATE_INTERVAL_TEXT.name())), 0,
                1);
        updateInterval = new TextArea();
        updateInterval.setMaxSize(100,30);
        updateInterval.setMinSize(100,30);
        settingsPane.add(updateInterval, 1, 1);

        settingsPane.add(
                new Text(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CONTINUOUS_RUN_TEXT.name())), 0,
                2);
        continuousRun = new CheckBox();
        settingsPane.add(continuousRun, 1, 2);


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

    //TODO find a way to make unique dialogs appear that way the data is unique per algorithm
    public enum Option{
        CONFIRM("Confirm");


        @SuppressWarnings("unused")
        private String option;

        Option(String option){this.option = option;}
    }
}
