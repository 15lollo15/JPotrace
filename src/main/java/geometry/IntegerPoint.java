package geometry;

public class IntegerPoint {
    private int x;
    private int y;

    public IntegerPoint() {}

    public IntegerPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public IntegerPoint copy() {
        return new IntegerPoint(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
