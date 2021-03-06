package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.io.IOException;
import java.util.ArrayList;

import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;


/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate{

    /**
     * The application to which this class of actions belongs.
     */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                   scrnshotButton; // toolbar button to take a screenshot of the data
    private LineChart<Number,Number> chart;          // the chart where data will be displayed
    private Button                   displayButton;  // workspace button to display data on the chart
    private TextArea                 textArea;       // text area for new data input
    private boolean                  hasNewText;     // whether or not the text area has any new data since last display
    private CheckBox                 checkBox;       // when toggled this determines the Read Only mode
    private String                   CSS_RESOURCE_PATH; // this is the css for the application


    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate){
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
        CSS_RESOURCE_PATH = applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CSS_RESOURCE_PATH.name());
    }

    public LineChart<Number,Number> getChart(){ return chart; }

    public TextArea getTextArea()             { return textArea; }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate){
        super.setResourcePaths(applicationTemplate);
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate){
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = SEPARATOR + String.join(SEPARATOR, manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                                                   manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String scrnshoticonPath =
                String.join(SEPARATOR, iconsPath, manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ICON.name()));
        scrnshotButton =
                setToolbarButton(scrnshoticonPath, manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_TOOLTIP.name()),
                                 true);
        toolBar.getItems().add(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate){
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> {
            applicationTemplate.getActionComponent().handleSaveRequest();
            saveButton.setDisable(true);
        });
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());

        scrnshotButton.setOnAction(e -> {
            try{
                ((AppActions) (applicationTemplate.getActionComponent())).handleScreenshotRequest();
            }
            catch (IOException e1){
                ErrorDialog screenShotError = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                screenShotError.show(
                        applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ERROR_TITLE.name()),
                        applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ERROR_MESSAGE.name()));
            }
        });
    }

    @Override
    public void initialize(){
        layout();
        setWorkspaceActions();
        appPane.getStylesheets().add(CSS_RESOURCE_PATH); // This is where I add the css sheet
    }

    @Override
    public void clear(){
        textArea.clear();
        chart.getData().clear();
        scrnshotButton.setDisable(true);
    }

    public String getCurrentText(){ return textArea.getText(); }

    private void layout(){
        PropertyManager manager = applicationTemplate.manager;

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(manager.getPropertyValue(AppPropertyTypes.CHART_TITLE.name()));

        VBox leftPanel = new VBox(8);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new Insets(10));

        VBox.setVgrow(leftPanel, Priority.ALWAYS);
        leftPanel.setMaxSize(windowWidth * 0.29, windowHeight * 0.3);
        leftPanel.setMinSize(windowWidth * 0.29, windowHeight * 0.3);

        Text leftPanelTitle = new Text(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLE.name()));
        String fontname = manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLEFONT.name());
        Double fontsize = Double.parseDouble(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLESIZE.name()));
        leftPanelTitle.setFont(Font.font(fontname, fontsize));

        textArea = new TextArea();

        HBox processButtonsBox = new HBox();
        displayButton = new Button(manager.getPropertyValue(AppPropertyTypes.DISPLAY_BUTTON_TEXT.name()));
        HBox.setHgrow(processButtonsBox, Priority.ALWAYS);
        StackPane centerCheckbox = new StackPane();
        checkBox = new CheckBox(manager.getPropertyValue(AppPropertyTypes.CHECKBOX_TEXT.name()));
        centerCheckbox.getChildren().add(checkBox);
        processButtonsBox.getChildren().addAll(displayButton, centerCheckbox);

        leftPanel.getChildren().addAll(leftPanelTitle, textArea, processButtonsBox);

        StackPane rightPanel = new StackPane(chart);
        rightPanel.setMaxSize(windowWidth * 0.69, windowHeight * 0.69);
        rightPanel.setMinSize(windowWidth * 0.69, windowHeight * 0.69);
        StackPane.setAlignment(rightPanel, Pos.CENTER);

        workspace = new HBox(leftPanel, rightPanel);
        HBox.setHgrow(workspace, Priority.ALWAYS);

        appPane.getChildren().add(workspace);
        VBox.setVgrow(appPane, Priority.ALWAYS);
    }

    private void setWorkspaceActions(){
        setTextAreaActions();
        setDisplayButtonActions();
        setCheckboxActions();
    }

    private void setTextAreaActions(){
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            ArrayList<String> arrayList = ((AppActions) applicationTemplate.getActionComponent()).getArrayList();
            try{
                if (!newValue.equals(oldValue)){
                    if (!newValue.isEmpty()){
                        ((AppActions) applicationTemplate.getActionComponent()).setIsUnsavedProperty(true);
                        if (newValue.charAt(newValue.length() - 1) == '\n') hasNewText = true;
                        newButton.setDisable(false);
                        saveButton.setDisable(false);

                        String[] oldTextArea = oldValue.split("\n");
                        String[] newTextArea = newValue.split("\n");
                        if (newTextArea.length < oldTextArea.length && arrayList.size() > 10){
                            textArea.clear();
                            int counter = 0;
                            for (String s : newTextArea){
                                arrayList.set(counter++, s);
                            }
                            for (; counter < oldTextArea.length; counter++){
                                arrayList.remove(counter);
                            }

                            ((AppData) applicationTemplate.getDataComponent()).loadInTenLines();
                        }
                    }
                    else{
                        hasNewText = true;
                        newButton.setDisable(true);
                        saveButton.setDisable(true);
                    }
                }
            }
            catch (IndexOutOfBoundsException e){
                System.err.println(newValue);
            }
        });
    }

    private void setDisplayButtonActions(){
        displayButton.setOnAction(event -> {
            if (hasNewText){
                try{
                    chart.getData().clear();
                    AppData dataComponent = (AppData) applicationTemplate.getDataComponent();
                    dataComponent.clear();
                    StringBuilder tempString = new StringBuilder();
                    ArrayList<String> arrayList =
                            ((AppActions) applicationTemplate.getActionComponent()).getArrayList();
                    String[] text = textArea.getText().split("\n");

                    if (arrayList.isEmpty()){
                        for (String s : text){
                            arrayList.add(s + "\n");
                        }
                    }

                    if (text.length > arrayList.size()){
                        int counter = 0;
                        for (int i = 0; i < arrayList.size(); i++){
                            arrayList.set(i, text[i]);
                            counter++;
                        }
                        for (; counter < text.length; counter++){
                            arrayList.add(text[counter]);
                        }
                    }

                    arrayList.forEach(tempString::append);
                    dataComponent.loadData(tempString.toString());
                    dataComponent.displayData();
                    addTooltips();
                    for (XYChart.Data<Number,Number> data : chart.getData().get(0).getData()){
                        StackPane node = (StackPane) data.getNode();
                        node.setVisible(false);
                    }
                    scrnshotButton.setDisable(false);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    private void setCheckboxActions(){
        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()){ textArea.setDisable(true); }
            else{ textArea.setDisable(false); }
        });
    }

    private void addTooltips(){
        for (int i = 1; i < chart.getData().size(); i++){
            for (XYChart.Data<Number,Number> data : chart.getData().get(i).getData()){
                Node node = data.getNode();
                node.setCursor(Cursor.HAND);
                Tooltip t = new Tooltip(data.getExtraValue().toString());
                t.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_BOTTOM_RIGHT);
                Tooltip.install(node, t);
            }
        }
    }

}
