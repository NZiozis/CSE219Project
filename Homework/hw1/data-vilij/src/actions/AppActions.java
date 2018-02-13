package actions;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.templates.ApplicationTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;

    /** Path to the data file currently active. */
    Path dataFilePath;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        // TODO for homework 1
        if (promptToSave()) {
            // this is what should happen if yes or no are selected
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE.name()));
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            applicationTemplate.manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name()),
                            applicationTemplate.manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name())
                    )
            );
            File file = fileChooser.showSaveDialog(new Stage());
            // now that I have this file how do I actually save it
        }
        else {
            //this is what should happen otherwise
        }
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleExitRequest() {
        // TODO for homework 1
        Platform.exit();
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
    }

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
    private boolean promptToSave() /*throws IOException*/ { // why is there a throws IOException here
        // TODO for homework 1

        ConfirmationDialog confirmationDialog = ConfirmationDialog.getDialog();
        confirmationDialog.show(
                applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE.name()),
                applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK.name())
        );
        ConfirmationDialog.Option chosen = confirmationDialog.getSelectedOption();
        if (chosen.equals(ConfirmationDialog.Option.CANCEL))
            return false;
        else
            return true;

    }
}
