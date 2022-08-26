public class DoublePoint {
    public double x;
    public double y;

    public DoublePoint() {}

    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public DoublePoint(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    public DoublePoint copy() {
        return new DoublePoint(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
