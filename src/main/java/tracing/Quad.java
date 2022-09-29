package tracing;

public class Quad {
    public double[] data = {0,0,0,0,0,0,0,0,0};

    public double at(int x, int y) {
        return this.data[x * 3 + y];
    }
}
