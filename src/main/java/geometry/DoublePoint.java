package geometry;

public class DoublePoint {
    private double x;
    private double y;

    public DoublePoint() {}

    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public DoublePoint(IntegerPoint point) {
        this.x = point.getX();
        this.y = point.getY();
    }

    public DoublePoint copy() {
        return new DoublePoint(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
