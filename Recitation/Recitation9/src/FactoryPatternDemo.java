public class FactoryPatternDemo{

    public static void main(String... args){

        ShapeFactory factory = new ShapeFactory();
        Circle circle = factory.getShape(ShapeType.Circle());
        Shape rectangle = factory.getShape(ShapeType.Rectangle());
        Shape square = factory.getShape(ShapeType.Square);

        circle.draw();
        rectangle.draw();
        square.draw();

    }


}

