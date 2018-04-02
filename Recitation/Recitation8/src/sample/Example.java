package sample;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;


public class Example {
    static final ProgressBar bar = new ProgressBar();

    public static void main(String... args) {
        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                final int counter = i;
                Platform.runLater(() -> bar.setProgress(counter / 1000000.0));
            }
        }).start();
    }
}



