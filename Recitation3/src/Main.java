package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Main extends Application {

    private static final String CSS_RESOURCE_PATH   = "/recitation3.css";
    private static final String FILE_INPUT_TOOLTIP  = "Enter absolute path of file to be uploaded.";
    private static final String FILE_DISPLAY_PROMPT = "File contents will be displayed here.";

    VBox     leftPanel;
    VBox     rightPanel;
    TextArea fileTextArea;
    Button   displayAsColumns;

    @Override
    public void start(Stage primaryStage) throws Exception {
        setFileTextArea();
        setLeftPanel();
        setRightPanel();

        HBox root = new HBox(leftPanel, rightPanel);

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource(CSS_RESOURCE_PATH).toExternalForm());

        leftPanel.prefWidthProperty().bind(scene.widthProperty().multiply(0.45));
        rightPanel.prefWidthProperty().bind(scene.widthProperty().multiply(0.5));


        primaryStage.setTitle("Spreadsheet Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setFileTextArea() {
        fileTextArea = new TextArea();
        fileTextArea.setWrapText(false);
        fileTextArea.setDisable(true);
        fileTextArea.setPromptText(FILE_DISPLAY_PROMPT);

    }

    private void setLeftPanel() {
        leftPanel = new VBox();
        leftPanel.setSpacing(10);
        leftPanel.setPadding(new Insets(10));

        TextField filePathInputNode = new TextField();
        Button    openButton        = new Button("Open File");

        filePathInputNode.setTooltip(new Tooltip(FILE_INPUT_TOOLTIP));
        openButton.setOnAction(event -> {
            String pathString = filePathInputNode.getText().trim();
            try {
                String textString = new String(Files.readAllBytes(Paths.get(pathString)));
                fileTextArea.setText(textString);
                displayAsColumns.setDisable(false);
            } catch (IOException e) {
                System.err.println(String.format("No such path: %s", pathString));
            }
        });

        leftPanel.getChildren().addAll(filePathInputNode, openButton);
    }

    private void setRightPanel() {
        rightPanel = new VBox();
        rightPanel.setSpacing(10);
        rightPanel.setPadding(new Insets(10));
        displayAsColumns = new Button("Display as Columns");

        HBox formatButtonsBox = new HBox(10);

        MenuButton arrange   = new MenuButton("Arrange");
        MenuItem   firstName = new MenuItem("First Name");
        MenuItem   lastName  = new MenuItem("Last Name");
        MenuItem   gpa       = new MenuItem("GPA");
        arrange.getItems().addAll(firstName, lastName, gpa);
        formatButtonsBox.getChildren().addAll(displayAsColumns, arrange);
        arrange.setDisable(true);
        displayAsColumns.setDisable(true);

        displayAsColumns.setOnAction(event -> {
            String   fourwhitespaces = "    ";
            String   text            = fileTextArea.getText();
            String[] lines           = text.split("\n");
            int fstColumnWidth = Arrays.stream(lines)
                                       .map(line -> line.split(",")[0].trim().length()).reduce(0, Math::max);
            int sndColumnWidth = Arrays.stream(lines)
                                       .map(line -> line.split(",")[1].trim().length()).reduce(0, Math::max);
            int lstColumnWidth = Arrays.stream(lines)
                                       .map(line -> line.split(",")[2].trim().length()).reduce(0, Math::max);

            StringBuilder textBuilder = new StringBuilder();
            Arrays.stream(lines).forEach(line -> {
                String[] cells = line.split(",");
                textBuilder.append(String.format("%1$-" + fstColumnWidth + "s", cells[0])).append(fourwhitespaces);
                textBuilder.append(String.format("%1$-" + sndColumnWidth + "s", cells[1])).append(fourwhitespaces);
                textBuilder.append(String.format("%1$-" + lstColumnWidth + "s", cells[2]));
                textBuilder.append('\n');
            });
            fileTextArea.setText(textBuilder.toString());
            arrange.setDisable(false);
        });

        firstName.setOnAction(event -> {
            //noinspection RepeatedSpace
            String              fourwhitespaces = "    ";
            String              text            = fileTextArea.getText();
            String[]            lines           = text.split("\n");
            Map<String, String> fstNameMap      = new TreeMap<>();
            Arrays.stream(lines).skip(1).forEach(line -> {
                String[] keyvalue = line.split(fourwhitespaces, 2);
                fstNameMap.put(keyvalue[0], keyvalue[1]);
            });

            StringBuilder textBuilder = new StringBuilder(lines[0]).append('\n');
            fstNameMap.forEach((key, value) -> textBuilder.append(key)
                                                          .append(fourwhitespaces)
                                                          .append(value)
                                                          .append('\n'));
            fileTextArea.setText(textBuilder.toString());
        });

        gpa.setOnAction(event -> {
            String            fourwhitespaces = "    ";
            String            text            = fileTextArea.getText();
            String[]          lines           = text.split("\n");
            Map<String, String> gpaMap        = new TreeMap<>();
            Arrays.stream(lines).skip(1).forEach(line -> {
                String[] keyvalue = line.split(fourwhitespaces, 6);
                String[] restVal = Arrays.copyOfRange(keyvalue, 0, 3);
                gpaMap.put(keyvalue[4], Arrays.toString(restVal));
            });

            StringBuilder textBuilder = new StringBuilder(lines[0]).append('\n');
            gpaMap.forEach((key, value) -> textBuilder.append(key)
                    .append(fourwhitespaces)
                    .append(value)
                    .append('\n'));
            fileTextArea.setText(textBuilder.toString());

        });

        rightPanel.getChildren().addAll(fileTextArea, formatButtonsBox);
    }

}
