package datastructures;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;

import java.util.Arrays;
import java.util.List;

public class ConfigurationDialog extends Stage implements Dialog{

    private static ConfigurationDialog       dialog;
    private        ConfirmationDialog.Option selectedOption;

    private ConfigurationDialog(){ /*empty constructor*/}

    public static ConfigurationDialog getDialog(){
        if (dialog == null) dialog = new ConfigurationDialog();
        return dialog;
    }

    @Override
    public void show(String dialogTitle, String message){


        //This is left here as a reminder that the previous options may just be saved without any effort on my part.
//        deleteOptionHistory();           // delete any previously selected option


        setTitle(dialogTitle);           // set the title of the dialog

//        This is where you would put the content that is to be in the dialog
//        setConfirmationMessage(message); // set the main error message

        showAndWait();                   // open the dialog and wait for the user to click the close button
    }

    @Override
    public void init(Stage owner){

        initModality(Modality.WINDOW_MODAL); // modal => messages are blocked from reaching other windows
        initOwner(owner);

        List<Button> buttons = Arrays.asList(new Button(ConfirmationDialog.Option.YES.name()),
                                             new Button(ConfirmationDialog.Option.NO.name()),
                                             new Button(ConfirmationDialog.Option.CANCEL.name()));

        buttons.forEach(button -> button.setOnAction((ActionEvent event) -> {
            this.selectedOption = ConfirmationDialog.Option.valueOf(((Button) event.getSource()).getText());
            this.hide();
        }));

        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().addAll(buttons);

        VBox messagePane = new VBox(confirmationMessage, buttonBox);
        messagePane.setAlignment(Pos.CENTER);
        messagePane.setPadding(new Insets(10, 20, 20, 20));
        messagePane.setSpacing(10);

        this.setScene(new Scene(messagePane));

    }

    public enum Option{
        CONFIRM("Confirm");

        private String option;

        Option(String option){this.option = option;}
    }
}
