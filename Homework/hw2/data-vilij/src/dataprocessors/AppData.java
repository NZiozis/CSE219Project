package dataprocessors;

import actions.AppActions;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.ConfirmationDialog;
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
import java.util.HashMap;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {


    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        int counter = loadInTenLines();
        ArrayList<String> arrayList = ((AppActions) applicationTemplate.getActionComponent()).getArrayList();

        ErrorDialog dialog                 = (ErrorDialog)applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager            = applicationTemplate.manager;
        String          loadActionTitle    = manager.getPropertyValue(AppPropertyTypes.LOAD_ACTION_RESULT_TITLE.name());
        int             arrayListSize      = arrayList.size();
        String          loadActionResult   = manager.getPropertyValue(AppPropertyTypes.LOAD_ACTION_RESULT.name());
        loadActionResult = String.format(loadActionResult, arrayListSize, counter);

        dialog.show(loadActionTitle, loadActionResult);

    }

    public int loadInTenLines(){
        ArrayList<String> arrayList = ((AppActions) applicationTemplate.getActionComponent()).getArrayList();
        int counter = 0;
        for (String s : arrayList) {
            if (!(s.equals(""))) {
                ((AppUI) applicationTemplate.getUIComponent()).getTextArea().appendText(s);
                counter++;
            }
            if (counter == 10)
                break;
        }
        return counter;
    }

    public void loadData(String dataString) {
        try {
            processor.processString(dataString);
        } catch (Exception e) {
            ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager  = applicationTemplate.manager;
            String          errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
            String          errMsg   = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
            String          errInput = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
            dialog.show(errTitle, errMsg + errInput);
        }
    }

    @Override
    public void saveData(Path dataFilePath) {
        // NOTE: completing this method was not a part of HW 1. You may have implemented file saving from the
        // confirmation dialog elsewhere in a different way.
        try (PrintWriter writer = new PrintWriter(Files.newOutputStream(dataFilePath))) {
            writer.write(((AppUI) applicationTemplate.getUIComponent()).getCurrentText());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }

    /** This returns the index of an error if there is. If not, the Tuple.get_key() == -1 */
    public Tuple<Integer, String> indexOfErrorOrDuplicates(ArrayList<String> arrayList){
        Tuple<Integer,String> tuple = new Tuple<>(-1, "");
        HashMap<String, Integer> map = new HashMap<>();
        arrayList.forEach(string -> {
            if (map.containsKey(string)) {
                tuple.set_key((arrayList.indexOf(string)));
                tuple.set_value(string);
                tuple.set_isDuplicate(true);
            }
            else { map.put(string, 1); }
        });

        if (tuple.get_key() != -1) return tuple;

        arrayList.forEach(string -> {
            try {
                processor.processString(string);
            }
            catch (Exception e){
                tuple.set_key(arrayList.indexOf(string));
                tuple.set_value(string);
                tuple.set_isDuplicate(false);
            }

        });

        return tuple;
    }

    public class Tuple<T, T1> {
        T _key;
        T1 _value;
        boolean _isDuplicate;

        public Tuple(T key,T1 value){
            _key = key;
            _value = value;
        }


        public void set_isDuplicate(boolean _isDuplicate) { this._isDuplicate = _isDuplicate; }
        public void set_key(T _key) { this._key = _key; }
        public void set_value(T1 _value) { this._value = _value; }

        public T get_key() { return _key; }
        public T1 get_value() { return _value; }
        public boolean get_isDuplicate() { return _isDuplicate; }
    }

}
