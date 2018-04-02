package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    static final ProgressBar bar = new ProgressBar();

    @Override
    public void start(Stage primaryStage) throws Exception{
        StackPane pane = new StackPane();
        pane.getChildren().addAll(bar);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(pane, 300, 275));
        primaryStage.show();
    }

//    public static void main(String[] args) {
//
//        new Thread(() -> {
//            for (int i = 0; i < 10000; i++) {
//                final int counter = i;
//                Platform.runLater(() -> bar.setProgress(counter / 1000.0));
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//        launch(args);
//    }

    public static void main(String... args) {
        ProgressBar bar = new ProgressBar();
        Task task = new Task() {
            @Override
            protected Integer call() throws Exception {
                int iterations;
                for (iterations = 0; iterations < 100000000; iterations++) {
                    updateProgress(iterations, 100000000);
                }
                return iterations;
            }
        };
        bar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();

        launch(args);
    }

}
