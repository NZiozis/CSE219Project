public class ShapeFactory{

    public Shape getShape(ShapeType st){

        switch (st){
            case CIRCLE:
                return new Circle();
            case RECTANGLE:
                return new Rectangle();
            case SQUARE:
                return new Square();
            default:
                return null;
        }

    }

}

