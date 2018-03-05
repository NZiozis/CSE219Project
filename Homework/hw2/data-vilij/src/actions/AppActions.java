package actions;

import dataprocessors.AppData;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.image.WritableImage;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

import static vilij.settings.PropertyTypes.SAVE_WORK_TITLE;
import static vilij.templates.UITemplate.SEPARATOR;

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

    /** The boolean property marking whether or not there are any unsaved changes. */
    SimpleBooleanProperty isUnsaved;

    /** This will contain the contents that are currently being loaded in */
    ArrayList<String> arrayList = new ArrayList<>();

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
        this.isUnsaved = new SimpleBooleanProperty(false);
    }

    public void setIsUnsavedProperty(boolean property) { isUnsaved.set(property); }

    @Override
    public void handleNewRequest() {
        try {
            if (!isUnsaved.get() || promptToSave()) {
                applicationTemplate.getDataComponent().clear();
                applicationTemplate.getUIComponent().clear();
                isUnsaved.set(false);
                dataFilePath = null;
            }
        } catch (IOException e) { errorHandlingHelper(); }
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
        try {
            if (!isUnsaved.get() || promptToSave()) {
                isUnsaved.set(false);
            }
        }
        catch (IOException e) { errorHandlingHelper(); }
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
        try {
            if (promptToLoad())
                isUnsaved.set(false);
        }
        catch (IOException e) {errorHandlingHelper();}
    }

    @Override
    public void handleExitRequest() {
        try {
            if (!isUnsaved.get() || promptToSave())
                System.exit(0);
        } catch (IOException e) { errorHandlingHelper(); }
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        PropertyManager manager = applicationTemplate.manager;
        LineChart<Number,Number> chart = ((AppUI)applicationTemplate.getUIComponent()).getChart();
        WritableImage image = chart.snapshot(new SnapshotParameters(), null);

        FileChooser fileChooser = new FileChooser();
        String      dataDirPath = SEPARATOR + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
        URL         dataDirURL  = getClass().getResource(dataDirPath);

        if (dataDirURL == null)
            throw new FileNotFoundException(manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));

        fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
        fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));
        String description = manager.getPropertyValue(AppPropertyTypes.SCRNSHT_FILE_DESC.name());
        String extension   = manager.getPropertyValue(AppPropertyTypes.SCRNSHT_FILE_EXT.name());
        ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                String.format("*.%s", extension));

        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName(manager.getPropertyValue(AppPropertyTypes.SCRNSHT_INITIAL.name()));
        File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

        if (selected != null)
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), AppPropertyTypes.SCRNSHT_FILE_EXT.name(), selected);
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
    private boolean promptToSave() throws IOException {
        PropertyManager    manager = applicationTemplate.manager;
        ConfirmationDialog dialog  = ConfirmationDialog.getDialog();
        dialog.show(manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE.name()),
                    manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK.name()));

        if (dialog.getSelectedOption() == null) return false; // if user closes dialog using the window's close button

        if (dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES)) {
            if (dataFilePath == null) {
                FileChooser fileChooser = new FileChooser();
                String      dataDirPath = SEPARATOR + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
                URL         dataDirURL  = getClass().getResource(dataDirPath);

                if (dataDirURL == null)
                    throw new FileNotFoundException(manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));

                fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
                fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));

                String description = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name());
                String extension   = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name());
                ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                                                                String.format("*%s", extension));

                fileChooser.getExtensionFilters().add(extFilter);
                fileChooser.setInitialFileName(manager.getPropertyValue(AppPropertyTypes.DATA_FILE_INITIAL.name()));
                File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
                if (selected != null) {
                    dataFilePath = selected.toPath();
                    save();
                } else return false; // if user presses escape after initially selecting 'yes'
            } else
                save();
        }

        return !dialog.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL);
    }

    private boolean promptToLoad() throws IOException {
        PropertyManager    manager = applicationTemplate.manager;
        ConfirmationDialog dialog  = ConfirmationDialog.getDialog();
        dialog.show(manager.getPropertyValue(AppPropertyTypes.LOAD_DATA_TITLE.name()),
                manager.getPropertyValue(AppPropertyTypes.LOAD_DATA.name()));

        if (dialog.getSelectedOption() == null) return false;

        if (dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES)) {

            FileChooser fileChooser = new FileChooser();
            String      dataDirPath = SEPARATOR + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
            URL         dataDirURL  = getClass().getResource(dataDirPath);

            if (dataDirURL == null)
                throw new FileNotFoundException(manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));

            fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
            fileChooser.setTitle(manager.getPropertyValue(AppPropertyTypes.LOAD_DATA_TITLE.name()));

            String description = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name());
            String extension   = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name());
            ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                                                            String.format("*%s", extension));

            fileChooser.getExtensionFilters().add(extFilter);
            File selected = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
            if (selected != null) {
                Scanner s = new Scanner(new File(selected.toPath().toString())).useDelimiter("\n");
                while (s.hasNextLine()) {
                    arrayList.add(s.nextLine() + "\n");
                }

                AppData.Tuple<Integer,String> errorTuple =
                        ((AppData)applicationTemplate.getDataComponent()).indexOfErrorOrDuplicates(arrayList);

                if (errorTuple.get_key() == -1){
                    dataFilePath = selected.toPath();
                    load();
                }
                else {
                    dataFilePath=null;
                    if (errorTuple.get_isDuplicate()) { duplicateErrorHelper(errorTuple); }
                    else                              { invalidTextErrorHelper(errorTuple); }
                }

            }
            else return false; // if user presses escape after initially selecting 'yes'
        }

        return !dialog.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL);
    }

    private void save() throws IOException {
        applicationTemplate.getDataComponent().saveData(dataFilePath);
        isUnsaved.set(false);
    }

    private void load() throws IOException {
        applicationTemplate.getDataComponent().loadData(dataFilePath);
        isUnsaved.set(false);
    }

    private void errorHandlingHelper() {
        ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager  = applicationTemplate.manager;
        String          errTitle = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name());
        String          errMsg   = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
        String          errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
        dialog.show(errTitle, errMsg + errInput);
    }

    private void invalidTextErrorHelper(AppData.Tuple<Integer,String> tuple){
        ErrorDialog     dialog         = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager        = applicationTemplate.manager;
        String          errTitle       = manager.getPropertyValue(AppPropertyTypes.INVALID_TEXT_ERROR_TITLE.name());
        String          errMsg         = manager.getPropertyValue(AppPropertyTypes.INVALID_TEXT_ERROR_MESSAGE.name());
        String          invalidElement = tuple.get_value();
        String          online         = manager.getPropertyValue(AppPropertyTypes.ON_LINE.name());
        String          invalidIndex   = tuple.get_key().toString();
        dialog.show(errTitle, errMsg + invalidElement + online + invalidIndex);
    }

    private void duplicateErrorHelper(AppData.Tuple<Integer,String> tuple){
        ErrorDialog     dialog           = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager          = applicationTemplate.manager;
        String          errTitle         = manager.getPropertyValue(AppPropertyTypes.DUPLICATE_ERROR_TITLE.name());
        String          errMsg           = manager.getPropertyValue(AppPropertyTypes.DUPLICATE_ERROR_MESSAGE.name());
        String          duplicateElement = tuple.get_value();
        String          onLine           = manager.getPropertyValue(AppPropertyTypes.ON_LINE.name());
        String          duplicateIndex   = tuple.get_key().toString();

        dialog.show(errTitle, errMsg + duplicateElement + onLine + duplicateIndex);
    }

}
