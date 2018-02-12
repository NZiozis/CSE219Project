package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;
import vilij.settings.PropertyTypes;
import settings.AppPropertyTypes;


/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    protected String                     scrnshoticonPath;// This is the path for the scrnshoticonButton

    public ScatterChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;

        String SEPARATOR = "/";
        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(PropertyTypes.GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(PropertyTypes.ICONS_RESOURCE_PATH.name()));
        scrnshoticonPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ICON.name()));
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        // TODO for homework 1
        super.setToolBar(applicationTemplate); // This is temporary until I add the screenshot button
        scrnshotButton = setToolbarButton(scrnshoticonPath, AppPropertyTypes.SCREENSHOT_TOOLTIP.name(), true);
        toolBar = new ToolBar(newButton, saveButton, loadButton, printButton, exitButton, scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        //scrnshotButton.setOnAction(e -> applicationTemplate.getActionComponent().handleScreenshotRequest()); // complete the screenshot button action connection here.
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        // TODO for homework 1
        textArea.clear();
    }

    private void layout() {
        // TODO for homework 1
        workspace = new VBox(); // This creates the basic pane that the entire app will be based on
        Pane mainDataVisualization = new HBox(); // This is the main area that will hold the text area and chart

        // This is where the left hand side of the front end is created
        Pane LHS = new VBox();

        Pane centerLabel1 = new StackPane();
        Label dataFileLabel = new Label("Data File");
        centerLabel1.getChildren().add(dataFileLabel); // This is done to center the text of dataFieldLabel

        textArea = new TextArea();
        displayButton = new Button("Display");

        LHS.getChildren().addAll(centerLabel1, textArea, displayButton);


        //This is where the right hand side of the front end is created
        Pane RHS = new VBox();

        Pane centerLabel2 = new StackPane();
        Label dataVisualizationLabel = new Label("Data Visualization");
        centerLabel2.getChildren().add(dataVisualizationLabel); // This is done to center the text of dataVisualizationLabel

        NumberAxis xAxis = new NumberAxis(0, 10, 1);
        NumberAxis yAxis = new NumberAxis(0, 10, 1);
        chart = new ScatterChart<>(xAxis, yAxis);

        RHS.getChildren().addAll(centerLabel2, chart);

        //This is the main compilation of all the nodes back into the workspace
        mainDataVisualization.getChildren().addAll(LHS, RHS);
        workspace.getChildren().addAll(toolBar, mainDataVisualization); // this adds all the created nodes back into the original workspace Pane
        Scene scene = new Scene(workspace, super.windowWidth, super.windowHeight);

        primaryStage.setTitle("Data Visualization App");
        primaryStage.setScene(scene);
        primaryStage.show();
    } // fix the chartTitle. You should not have to create a new label.

    private void setWorkspaceActions() {
        // TODO for homework 1

        // with the display button, when it is clicked it will try and load the data, if not, it will run the error message
        displayButton.setOnAction(e -> {
            try {
                ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());
            }
            catch (Exception e1) {
               applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(
                       applicationTemplate.manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name()),
                       applicationTemplate.manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name()) +
                               e1.getLocalizedMessage()
               );
               clear();
            }
        });


    }

}