import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class AlgorithmTestSuite{
    //TODO fill out the test cases with valid data to make these testcases work

    @Test(expected = IllegalArgumentException.class)
    public void invalidArgsforRandomClassifier() throws ClassNotFoundException, IllegalAccessException,
                                                        InvocationTargetException, InstantiationException{
        Class<?> klass = Class.forName("algorithms.Classification.RandomClassifier");
        Constructor konstructor = klass.getConstructors()[0];
        konstructor.newInstance("1", -1, 23, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidArgsforKMeansCluster(){}

    @Test(expected = IllegalArgumentException.class)
    public void invalidArgsforRandomClusterer(){}
}
