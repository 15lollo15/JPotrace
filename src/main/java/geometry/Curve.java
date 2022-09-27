package geometry;

public class Curve {
    private DoublePoint[] c;
    private Tag[] tag;
    private DoublePoint[] vertex;
    private double[] alpha;
    private double[] beta;
    private boolean initializated;

    private int n;

    public Curve(int n) {
        this.n = n;
        vertex = new DoublePoint[n];
        tag = new Tag[n];
        c = new DoublePoint[n * 3];
        alpha = new double[n];
        beta = new double[n];
        initializated = false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Alpha[");
        for (double d : alpha)
            sb.append(d + ", ");
        sb.append("]");

        sb.append("\n Beta[");
        for (double d : beta)
            sb.append(d + ", ");
        sb.append("]");

        sb.append("\n Vertex");
        sb.append("[");
        for (DoublePoint v : vertex)
            sb.append(v + ", ");
        sb.append("]");

        sb.append("\n Tag");
        sb.append("[");
        for (Tag t : tag)
            sb.append(t + ", ");
        sb.append("]");

        sb.append("\n c");
        sb.append("[");
        for (DoublePoint t : c)
            sb.append(t + ", ");
        sb.append("]");

        return sb.toString();
    }

    public DoublePoint[] getC() {
        return c;
    }

    public Tag[] getTag() {
        return tag;
    }

    public DoublePoint[] getVertex() {
        return vertex;
    }

    public double[] getAlpha() {
        return alpha;
    }

    public double[] getBeta() {
        return beta;
    }

    public boolean isInitializated() {
        return initializated;
    }

    public void setIsInitializated(boolean initializated) {
        this.initializated = initializated;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }
}
