import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import vilij.templates.ApplicationTemplate;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AlgorithmTestSuite.class })
public class TestPrimer{
    ApplicationTemplate applicationTemplate;

    public TestPrimer(){ }


    @BeforeClass
    public void setUpApplicationTemplate(ApplicationTemplate applicationTemplate){
        this.applicationTemplate = applicationTemplate;
    }
}
