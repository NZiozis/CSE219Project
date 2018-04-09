public class FactoryPatternDemo{

    public static void main(String... args){

        ShapeFactory factory = new ShapeFactory();
        Shape circle = factory.getShape(ShapeType.CIRCLE);
        Shape rectangle = factory.getShape(ShapeType.RECTANGLE);
        Shape square = factory.getShape(ShapeType.SQUARE);

        circle.draw();
        rectangle.draw();
        square.draw();

    }


}

