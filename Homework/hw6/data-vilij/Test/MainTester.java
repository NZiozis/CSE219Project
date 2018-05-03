import org.junit.runner.JUnitCore;
import vilij.templates.ApplicationTemplate;

public class MainTester{

    ApplicationTemplate applicationTemplate;

    public MainTester(ApplicationTemplate applicationTemplate){
        this.applicationTemplate = applicationTemplate;
    }

    public static void main(String[] args){
        JUnitCore.runClasses(TestPrimer.class);
    }

}
