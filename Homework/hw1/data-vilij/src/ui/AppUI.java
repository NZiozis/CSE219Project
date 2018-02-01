package ui;

import actions.AppActions;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import javax.swing.text.LabelView;

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

    public ScatterChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        // TODO for homework 1
        super.setToolBar(applicationTemplate); // This is temporary until I add the screenshot button
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        // TODO for homework 1
    }

    private void layout() {
        // TODO for homework 1
        Pane root = new VBox(); // This creates the basic pane that the entire app will be based on
        Pane mainDataVisualization = new HBox(); // This is the main area that will hold the text area and chart

        // This is where the left hand side of the front end is created
        Pane LHS = new VBox(); //Update this later with something more representative of the left side
        Label dataFileLabel = new Label("Data File");
        textArea = new TextArea();
        Button display = new Button("Display");

        LHS.getChildren().addAll(dataFileLabel, textArea,display);

        //This is where the right hand side of the front end is created

        /*
        String test = "test";
        String two = "two";
        test.bind(two);
        scene.widthProperty().bind(scene.heightProperty());
        */

        mainDataVisualization.getChildren().addAll(LHS);
        root.getChildren().addAll(toolBar, mainDataVisualization); // this adds all the created nodes back into the original root Pane
        Scene scene = new Scene(root, 1000*1.5, 1000);

        primaryStage.setTitle("Data Visualization App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setWorkspaceActions() {
        // TODO for homework 1
    }
}