package sample;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Using reflection to identify which one of the three methods (all of which have identical return type, visibility, and
 * arguments) is checking for equality.
 */
public class ReflectionExamples{

    public static void main(String... args) throws ClassNotFoundException, IllegalAccessException,
                                                   InstantiationException, InvocationTargetException{
        // example of getting a nested class using forName()
        Class<?> klass = Class.forName("sample.ReflectionExamples$MyClass");
        Constructor konstructor = klass.getConstructors()[0];

        // example of how to create an instance using reflection
        MyClass mc1 = (MyClass) konstructor.newInstance(2, "two");
        MyClass mc2 = (MyClass) konstructor.newInstance(2, "Two");


        Method[] methods = klass.getDeclaredMethods();

        for (Method method : methods){

            boolean answer = (boolean) method.invoke(mc1, mc2);

            if (!answer){
                System.out.println(method.getName());
            }

            // I assume that equalsIgnoredCase is improper for checking equality in this case since it isn't possible
            // to distinguish the other two

        }


        // your code should be here (hint: you may need to create more instances beyond the one created above)
    }

    public static class MyClass{

        private int    myValue;
        private String myString;

        public MyClass(int val, String str){
            this.myValue = val;
            this.myString = str;
        }

        public boolean methodA(Object obj){
            return true;
        }

        public boolean methodB(Object obj){
            if (this == obj) return true;
            if (!(obj instanceof MyClass)) return false;
            MyClass that = (MyClass) obj;
            return (this.myValue == that.myValue && this.myString.equals(that.myString));
        }

        public boolean methodC(Object obj){
            if (this == obj) return true;
            if (!(obj instanceof MyClass)) return false;
            MyClass that = (MyClass) obj;
            return (this.myValue == that.myValue && this.myString.equalsIgnoreCase(that.myString));
        }
    }
}