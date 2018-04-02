package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.concurrent.locks.ReentrantLock;

public class MultithreadingJavaFX extends Application {
    private ProgressBar bar;
    private ProgressIndicator indicator;
    private Label processLabel;

    private int numTasks = 0;

    @Override
    public void start(Stage primaryStage) {
        VBox box = new VBox();
        HBox toolbar = new HBox();
        bar = new ProgressBar(0);
        indicator = new ProgressIndicator(0);
        indicator.setStyle("font-size: 16pt");
        toolbar.getChildren().add(bar);
        toolbar.getChildren().add(indicator);
        Button button = new Button("Restart");
        processLabel = new Label();
        processLabel.setFont(new Font("Serif", 16));
        box.getChildren().add(toolbar);
        box.getChildren().add(button);
        box.getChildren().add(processLabel);
        Scene scene = new Scene(box);
        primaryStage.setScene(scene);
        ReentrantLock lock = new ReentrantLock();

        button.setOnAction(e -> {
            Task task = new Task() {
                int task = numTasks++;
                double max = 200;
                double perc;

                @Override
                protected Void call() {
                    lock.lock();
                    try {
                        for (int i = 0; i < 200; i++) {
                            System.out.println(i);
                            perc = i / max;
                            Platform.runLater(() -> {
                                bar.setProgress(perc);
                                indicator.setProgress(perc);
                                processLabel.setText("Task number " + task);

                            });

                            // sleep each frame
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                            }

                        }
                        return null;
                    } finally {
                        lock.unlock();
                    }
                }

            };
            Thread thread = new Thread(task);
            thread.start();
        });
        primaryStage.show();
    }
}
