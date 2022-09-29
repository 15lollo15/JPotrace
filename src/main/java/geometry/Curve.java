package geometry;

public class Curve {
    private final DoublePoint[] controlPoints;
    private final Tag[] tag;
    private final DoublePoint[] vertex;
    private final double[] alpha;
    private final double[] beta;
    private boolean initialized;

    public Curve(int n) {
        vertex = new DoublePoint[n];
        tag = new Tag[n];
        controlPoints = new DoublePoint[n * 3];
        alpha = new double[n];
        beta = new double[n];
        initialized = false;
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
        for (DoublePoint t : controlPoints)
            sb.append(t + ", ");
        sb.append("]");

        return sb.toString();
    }

    public DoublePoint[] getControlPoints() {
        return controlPoints;
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

    public boolean isInitialized() {
        return initialized;
    }

    public void setIsInitializated(boolean initializated) {
        this.initialized = initializated;
    }

}
