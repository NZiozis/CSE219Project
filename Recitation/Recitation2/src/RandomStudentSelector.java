import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ritwik Banerjee
 */
public class RandomStudentSelector extends Application {

    List<String>              names;
    Map<String, List<String>> csv;
    Button nextButton = new Button("Next");
    int currentPosition;

    Button prevButton = new Button("Previous");
    Button pickButton = new Button("Random");


    final Label studentNameLabel = new Label();

    @Override
    public void start(Stage primaryStage) throws URISyntaxException, IOException {
        setCSV(Paths.get("/home/nikoziozis/CollegeClasses/CSE219/Recitation2/resources/roster.csv"));
        setNames();

        List<String> fontFamilies     = javafx.scene.text.Font.getFamilies();
        String       randomFontFamily = fontFamilies.get((int) (Math.random() * fontFamilies.size()));
        Font         buttonFont       = new Font(randomFontFamily, 24);
        prevButton.setFont(buttonFont);
        pickButton.setFont(buttonFont);
        nextButton.setFont(buttonFont);
        randomFontFamily = fontFamilies.get((int) (Math.random() * fontFamilies.size()));
        studentNameLabel.setFont(new Font(randomFontFamily, 48));

        FlowPane buttonToolbar = new FlowPane();
        buttonToolbar.setAlignment(Pos.CENTER);
        buttonToolbar.getChildren().add(prevButton);
        buttonToolbar.getChildren().add(pickButton);
        buttonToolbar.getChildren().add(nextButton);

        currentPosition = getRandomPosition();
        studentNameLabel.setText(names.get(currentPosition));

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.getChildren().add(buttonToolbar);
        root.getChildren().add(studentNameLabel);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);

        EventHandler<ActionEvent> buttonsHandler = event -> {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    int maxCount = 1;
                    if (event.getSource() == pickButton) {
                        maxCount = 15;
                    }
                    for (int i = 0; i < maxCount; i++) {
                        if (event.getSource() == prevButton) {
                            currentPosition -= 1;
                            if (currentPosition < 0)
                                currentPosition = names.size() - 1;
                        }
                        else if (event.getSource() == nextButton) {
                            currentPosition += 1;
                            if (currentPosition > names.size() - 1)
                                currentPosition = 0;
                        }
                        else currentPosition = getRandomPosition();

                        Platform.runLater(() -> {
                            String student = names.get(currentPosition);
                            studentNameLabel.setText(student);
                        });
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                    }
                    return null;
                }
            };
            Thread thread = new Thread(task);
            thread.start();
        };

        prevButton.setOnAction(buttonsHandler);
        pickButton.setOnAction(buttonsHandler);
        nextButton.setOnAction(buttonsHandler);

        primaryStage.setTitle("Random Selector");
        primaryStage.show();
    }

    private void setCSV(Path csvFilePath) throws IOException {
        csv = Files.lines(csvFilePath).map(l -> l.split(",", 2))
                   .collect(Collectors.toMap(a -> a[0], a -> Arrays.asList(a[1].split(","))));
    }

    private void setNames() {
        names = new ArrayList<>(csv.keySet());
        Collections.sort(names);
    }

    private int getRandomPosition() { return (int) (Math.random() * names.size()); }

}
