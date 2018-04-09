public class FactoryPatternDemo{

  public static void main(String...args){
    
    ShapeFactory factory = new ShapeFactory();

    ShapeType circle = factory.getShape(CIRCLE); 

  }


}

