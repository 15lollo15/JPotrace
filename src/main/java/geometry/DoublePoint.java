package geometry;

public class DoublePoint {
    public double x;
    public double y;

    public DoublePoint() {}

    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public DoublePoint(Point point) {
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
}
