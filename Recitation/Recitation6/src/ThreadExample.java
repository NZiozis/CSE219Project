
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class ThreadExample extends Application {

    public static final String START_TEXT = "Start";
    public static final String PAUSE_TEXT = "Pause";
    private Stage window;
    private BorderPane appPane;
    private FlowPane topPane;
    private Button startButton;
    private Button pauseButton;
    private ScrollPane scrollPane;
    private TextArea textArea;
    private Thread dateThread;
    private Task dateTask;
    private Thread counterThread;
    private Task counterTask;
    private boolean work;

    @Override
    public void start(Stage primaryStage) throws Exception {
	initLayout();
	initHandlers();
	initWindow(primaryStage);
	initThreads();
	work = false;
    }
    
    public void startWork() {
	work = true;
    }
    
    public void pauseWork() {
	work = false;
    }
    
    public boolean doWork() {
	return work;
    }

    public void appendText(String textToAppend) {
	textArea.appendText(textToAppend);
    }

    public void sleep(int timeToSleep) {
	try {
	    Thread.sleep(timeToSleep);
	} catch (InterruptedException ie) {
	    ie.printStackTrace();
	}
    }

    private void initLayout() {
	topPane = new FlowPane(Orientation.HORIZONTAL);
	startButton = new Button(START_TEXT);
	pauseButton = new Button(PAUSE_TEXT);
	topPane.getChildren().add(startButton);
	topPane.getChildren().add(pauseButton);

	textArea = new TextArea();
	scrollPane = new ScrollPane();
	scrollPane.setContent(textArea);

	appPane = new BorderPane();
	appPane.setTop(topPane);
	appPane.setCenter(scrollPane);
    }
    
    private void initHandlers() {
	startButton.setOnAction(new StartHandler(this));
	pauseButton.setOnAction(new PauseHandler(this));
    }

    private void initWindow(Stage initPrimaryStage) {
	window = initPrimaryStage;
	Scene scene = new Scene(appPane, 800, 600);
	window.setScene(scene);
	window.show();
    }

    private void initThreads() {
	dateTask = new DateTask(this);
	dateThread = new Thread(dateTask);
	dateThread.start();

	counterTask = new CounterTask(this);
	counterThread = new Thread(counterTask);
	counterThread.start();
    }

    public static void main(String[] args) {
	launch(args);
    }
}
