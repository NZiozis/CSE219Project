
import javafx.event.Event;
import javafx.event.EventHandler;

public class StartHandler implements EventHandler {
    private final ThreadExample app;
    
    public StartHandler(ThreadExample initApp) {
	app = initApp;
    }
    
    @Override
    public void handle(Event event) {
	app.startWork();
    }
    
}
