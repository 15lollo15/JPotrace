package tracing.base;

public class Quad {
    private double[] data = {0,0,0,0,0,0,0,0,0};

    public double at(int x, int y) {
        return this.data[x * 3 + y];
    }

    public double[] getData() {
        return data;
    }
}
