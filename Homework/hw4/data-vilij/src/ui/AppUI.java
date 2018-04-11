package ui;

import actions.AppActions;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.io.File;

import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate{


    // This will display the algorithm types and then the algorithms available to be chosen from as well as the option to configure them
    public  GridPane            loadedAlgorithms;
    /**
     * The application to which this class of actions belongs.
     */
    private ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                   scrnshotButton;   // toolbar button to take a screenshot of the data
    private Button                   editDoneButton;   // toolbar button to edit the textArea when inputting new data
    private LineChart<Number,Number> chart;            // the chart where data will be displayed
    private TextArea                 textArea;         // text area for new data input
    private boolean                  hasNewText;
    private Text                     loadedInFileText; // text displayed when
    private ToggleGroup              algorithms;       // this will hold the algorithms of the currently selected type.
    private Button                   selectButton;     // selected choice from radio buttons

    AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate){
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    public ToggleGroup getAlgorithms(){
        return algorithms;
    }

    public LineChart<Number,Number> getChart(){
        return chart;
    }

    public Button getEditDoneButton(){
        return editDoneButton;
    }

    public Button getScrnshotButton(){
        return scrnshotButton;
    }

    public TextArea getTextArea(){
        return textArea;
    }

    public void setLoadedInFileText(String text){
        this.loadedInFileText.setText(text);
    }

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
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        scrnshotButton.setOnAction(
                e -> ((AppActions) applicationTemplate.getActionComponent()).handleScreenshotRequest());
    }

    @Override
    public void initialize(){
        layout();
        setWorkspaceActions();
        appPane.getStylesheets()
                .add(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CSS_RESOURCE_PATH.name()));
    }

    @Override
    public void clear(){
        textArea.clear();
        chart.getData().clear();
    }

    public String getCurrentText(){
        return textArea.getText();
    }

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
        textArea.setMaxSize(windowWidth * 0.29, windowHeight * 0.25);
        textArea.setMinSize(windowWidth * 0.29, windowHeight * 0.25);
        textArea.setVisible(false);

        HBox processButtonsBox = new HBox();
        HBox.setHgrow(processButtonsBox, Priority.ALWAYS);

        loadedInFileText = new Text(manager.getPropertyValue(AppPropertyTypes.NO_DATA_LOADED_IN_PLACEHOLDER.name()));
        loadedInFileText.setWrappingWidth(leftPanel.getMaxWidth());
        loadedInFileText.visibleProperty().bind(textArea.visibleProperty());

        ScrollPane algorithmHouse = new ScrollPane();
        loadedAlgorithms = new GridPane();
        algorithmHouse.setContent(loadedAlgorithms);
        algorithmHouse.visibleProperty().bind(textArea.visibleProperty());

        algorithms = new ToggleGroup();
        selectButton = new Button(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SELECT_TEXT.name()));
        selectButton.visibleProperty().bind(textArea.visibleProperty());

        editDoneButton = new Button(manager.getPropertyValue(AppPropertyTypes.EDIT_TEXT.name()));
        editDoneButton.visibleProperty().bind(textArea.visibleProperty());

        leftPanel.getChildren()
                .addAll(leftPanelTitle, textArea, editDoneButton, processButtonsBox, loadedInFileText, algorithmHouse,
                        selectButton);

        leftPanelTitle.visibleProperty().bind(textArea.visibleProperty());

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
        setSelectButtonActions();
        editDoneButton.setOnAction(e -> ((AppActions) applicationTemplate.getActionComponent()).handleEditDone());
    }

    private void setTextAreaActions(){
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                if (!newValue.equals(oldValue)){
                    if (!newValue.isEmpty()){
                        ((AppActions) applicationTemplate.getActionComponent()).setIsUnsavedProperty(true);
                        if (newValue.charAt(newValue.length() - 1) == '\n') hasNewText = true;
                        newButton.setDisable(false);
                        saveButton.setDisable(false);
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

    private void setSelectButtonActions(){
        selectButton.setOnMouseClicked(event -> {
            RadioButton selectedToggle = (RadioButton) algorithms.getSelectedToggle();
            AppActions appActions = (AppActions) applicationTemplate.getActionComponent();
            File algorithmsDir =
                    new File(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHMS_PATH.name()));
            if (selectedToggle.getText()
                    .equals(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.BACK.name()))){
                appActions.populateAlgorithms(algorithms, algorithmsDir);
            }

            else{
                algorithmsDir = new File(algorithmsDir.toString() + "/" + selectedToggle.getText());
                appActions.populateAlgorithms(algorithms, algorithmsDir);
            }
        });
    }
}
