package ui;

import actions.AppActions;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.util.HashMap;

import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate{

    // This will display the algorithm types and then the algorithms available to be chosen from as well as the option to configure them
    /**
     * The application to which this class of actions belongs.
     */
    private ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                   scrnshotButton;      // toolbar button to take a screenshot of the data
    private Button                   editDoneButton;
    // toolbar button to edit the textArea when inputting new data
    private LineChart<Number,Number> chart;               // the chart where data will be displayed
    private TextArea                 textArea;            // text area for new data input
    private Text                     loadedInFileText;    // text displayed when
    private GridPane                 loadedAlgorithms;
    private ToggleGroup              algorithms;
    // this will hold the algorithms of the currently selected type.

    private Button                   selectButton;        // selected choice from radio buttons
    private Button                   runButton;
    private HashMap<String,GridPane> previouslyLoaded;    // contains gridpanes of algos loaded in past
    private ScrollPane               algorithmHouse;
    private StringBuilder            classPathtoAlgorithm;
    // this will contain the class path to the algorithm for Reflection purposes

    private SimpleBooleanProperty    configurationValid;
    private SimpleBooleanProperty    algorithmIsSelected;
    private SimpleBooleanProperty    dataLoadedIn;
    private RadioButton              selectedToggle;
    private Integer                  currentAlgoIndex;

    @SuppressWarnings("unused")
    private boolean hasNewText;

    AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate){
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
        previouslyLoaded = new HashMap<>();
        configurationValid = new SimpleBooleanProperty(false);
        algorithmIsSelected = new SimpleBooleanProperty(false);
        dataLoadedIn = new SimpleBooleanProperty(false);
        classPathtoAlgorithm = new StringBuilder();
        classPathtoAlgorithm
                .append(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHMS_PATH.name()));
    }

    public Integer getCurrentAlgoIndex(){
        return currentAlgoIndex;
    }

    public StringBuilder getClassPathtoAlgorithm(){
        return classPathtoAlgorithm;
    }

    public void setDataLoadedIn(boolean dataLoadedIn){
        this.dataLoadedIn.set(dataLoadedIn);
    }

    public void setConfigurationValid(boolean configurationValid){
        this.configurationValid.set(configurationValid);
    }

    public Button getSelectButton(){
        return selectButton;
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
        scrnshotButton
                .setOnAction(e -> ( (AppActions) applicationTemplate.getActionComponent() ).handleScreenshotRequest());
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
        previouslyLoaded.clear();
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
        textArea.setMaxSize(windowWidth * 0.25, windowHeight * 0.25);
        textArea.setMinSize(windowWidth * 0.25, windowHeight * 0.25);
        textArea.setVisible(false);

        HBox processButtonsBox = new HBox();
        HBox.setHgrow(processButtonsBox, Priority.ALWAYS);

        loadedInFileText = new Text(manager.getPropertyValue(AppPropertyTypes.NO_DATA_LOADED_IN_PLACEHOLDER.name()));
        loadedInFileText.setWrappingWidth(windowWidth * 0.25);
        loadedInFileText.visibleProperty().bind(textArea.visibleProperty());

        algorithmHouse = new ScrollPane();
        algorithmHouse.visibleProperty()
                      .bind(textArea.visibleProperty().and(textArea.disableProperty()).and(dataLoadedIn));
        algorithmHouse.setMaxSize(windowWidth * 0.25, windowHeight * 0.15);
        algorithmHouse.setMinSize(windowWidth * 0.25, windowHeight * 0.15);

        algorithms = new ToggleGroup();
        selectButton = new Button(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SELECT_TEXT.name()));
        selectButton.visibleProperty()
                    .bind(textArea.visibleProperty().and(textArea.disableProperty()).and(dataLoadedIn));
        selectButton.setDisable(true);

        editDoneButton = new Button(manager.getPropertyValue(AppPropertyTypes.EDIT_TEXT.name()));
        editDoneButton.visibleProperty().bind(textArea.visibleProperty());

        loadedAlgorithms = ( (AppActions) applicationTemplate.getActionComponent() ).populateAlgorithms(algorithms,
                                                                                                        applicationTemplate.manager
                                                                                                                .getPropertyValue(
                                                                                                                        AppPropertyTypes.ALGORITHMS_PATH
                                                                                                                                .name()));
        algorithmHouse.setContent(loadedAlgorithms);

        runButton = new Button(null, new ImageView(
                new Image(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.RUN_BUTTON_ICON_PATH.name()))));
        runButton.visibleProperty().bind(algorithmIsSelected.and(algorithmHouse.visibleProperty()));
        runButton.disableProperty().bind(( algorithmIsSelected.and(configurationValid) ).not()
                                                                                        .or(( (AppActions) applicationTemplate
                                                                                                .getActionComponent()
                                                                                            ).isRunningProperty()));
        leftPanel.getChildren()
                 .addAll(leftPanelTitle, textArea, editDoneButton, processButtonsBox, loadedInFileText, algorithmHouse,
                         selectButton, runButton);
        scrnshotButton.disableProperty()
                      .bind(( (AppActions) applicationTemplate.getActionComponent() ).isRunningProperty());

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
        editDoneButton.setOnAction(e -> ( (AppActions) applicationTemplate.getActionComponent() ).handleEditDone());
        runButton.setOnAction(e -> ( (AppActions) applicationTemplate.getActionComponent() ).handleRunRequest());
        saveButton.disableProperty()
                  .bind(( (AppActions) applicationTemplate.getActionComponent() ).isUnsavedProperty().not());
    }

    private void setTextAreaActions(){
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                if (!newValue.equals(oldValue)){
                    if (!newValue.isEmpty()){
                        ( (AppActions) applicationTemplate.getActionComponent() ).setIsUnsavedProperty(true);
                        if (newValue.charAt(newValue.length() - 1) == '\n') hasNewText = true;
                        newButton.setDisable(false);
                    }
                    else{
                        ( (AppActions) applicationTemplate.getActionComponent() ).setIsUnsavedProperty(false);
                        hasNewText = true;
                        newButton.setDisable(true);
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
            selectedToggle = (RadioButton) algorithms.getSelectedToggle();
            String algorithmsDir =
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHMS_PATH.name());

            try{
                if (previouslyLoaded.containsKey(selectedToggle.getText())){
                    loadInPreviouslyLoaded();
                }
                else{
                    loadInNew(algorithmsDir);
                }

            }
            /*This is ignored because it only throws this when the user fails to pick a radio button*/
            catch (NullPointerException ignored){}
        });

    }

    private void loadInPreviouslyLoaded() throws NullPointerException{
        loadedAlgorithms = previouslyLoaded.get(selectedToggle.getText());
        algorithmHouse.setContent(loadedAlgorithms);
        if (selectedToggle.getText()
                          .equals(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.BACK.name()))){
            classPathtoAlgorithm.delete(classPathtoAlgorithm.indexOf(
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLASS_PATH_JOINER.name())),
                                        classPathtoAlgorithm.length());
            algorithmIsSelected.set(false);
            configurationValid.set(false);
        }
        else{
            classPathtoAlgorithm
                    .append(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLASS_PATH_JOINER.name()))
                    .append(selectedToggle.getText());

            if (selectedToggle.getText().contains(
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLASS_FILE_EXT.name()))){
                currentAlgoIndex = loadedAlgorithms.getChildren().indexOf(selectedToggle) / 2 +
                                   1; // This equation accounts for the other elements that are inside of the GridPane
                algorithmIsSelected.set(true);
                configurationValid.set(false);
            }
            else{
                algorithmIsSelected.set(false);
                configurationValid.set(false);
            }
        }
    }

    private void loadInNew(String algorithmsDir) throws NullPointerException{
        GridPane temp;
        AppActions appActions = ( (AppActions) applicationTemplate.getActionComponent() );
        if (selectedToggle.getText()
                          .equals(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.BACK.name()))){
            classPathtoAlgorithm.delete(classPathtoAlgorithm.indexOf(
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLASS_PATH_JOINER.name())),
                                        classPathtoAlgorithm.length());
            temp = appActions.populateAlgorithms(algorithms, algorithmsDir);
            algorithmIsSelected.set(false);
            configurationValid.set(false);
        }

        else{

            classPathtoAlgorithm
                    .append(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLASS_PATH_JOINER.name()))
                    .append(selectedToggle.getText());

            /*This else if block is why I re-added the file extensions, if a better way becomes apparent, fix this*/
            if (selectedToggle.getText().contains(
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLASS_FILE_EXT.name()))){
                temp = loadedAlgorithms;
                currentAlgoIndex = temp.getChildren().indexOf(selectedToggle) / 2 + 1;
                classPathtoAlgorithm.delete(classPathtoAlgorithm.lastIndexOf(
                        applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLASS_PATH_JOINER.name())),
                                            classPathtoAlgorithm.length());
                algorithmIsSelected.set(true);
                configurationValid.set(false);
            }

            else{
                algorithmsDir = algorithmsDir + SEPARATOR + selectedToggle.getText();
                temp = appActions.populateAlgorithms(algorithms, algorithmsDir);
                algorithmIsSelected.set(false);
                configurationValid.set(false);
            }
        }

        loadedAlgorithms = temp;
        algorithmHouse.setContent(loadedAlgorithms);
        previouslyLoaded.put(selectedToggle.getText(), temp);
    }

}
