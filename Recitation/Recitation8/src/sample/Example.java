package sample;

import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Example {

//    This is the method that uses Threads



    //This is the main that uses the concurrent.Task<V>
//    public static void main(String... args) {
//        ProgressBar bar = new ProgressBar();
//
//        Task task = new Task() {
//            @Override
//            protected Integer call() throws Exception {
//                int iterations;
//                for (iterations = 0; iterations < 10000000; iterations++) {
//                    updateProgress(iterations, 10000000);
//                }
//                return iterations;
//            }
//        };
//        bar.progressProperty().bind(task.progressProperty());
//        new Thread(task).start();
//
//        pane.getChildren().addAll(bar);
//        stage.setScene(scene);
//        stage.show();
//    }
}



