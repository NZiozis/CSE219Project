
import javafx.event.Event;
import javafx.event.EventHandler;

public class PauseHandler implements EventHandler {
    private final ThreadExample app;
    
    public PauseHandler(ThreadExample initApp) {
	app = initApp;
    }
    
    @Override
    public void handle(Event event) {
	app.pauseWork();
    }
    
}
