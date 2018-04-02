package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    private static final ProgressBar bar = new ProgressBar();

    public static void main(String[] args) {

        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                final int counter = i;
                Platform.runLater(() -> bar.setProgress(counter / 1000.0));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StackPane pane = new StackPane();
        pane.getChildren().addAll(bar);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(pane, 300, 275));
        primaryStage.show();
    }

//    public static void main(String... args) {
//        ProgressBar bar = new ProgressBar();
//        Task task = new Task() {
//            @Override
//            protected Integer call() throws Exception {
//                int iterations;
//                for (iterations = 0; iterations < 100000000; iterations++) {
//                    updateProgress(iterations, 100000000);
//                }
//                return iterations;
//            }
//        };
//        bar.progressProperty().bind(task.progressProperty());
//        new Thread(task).start();
//
//        launch(args);
//    }

}
