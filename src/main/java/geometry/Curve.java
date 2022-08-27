package geometry;

public class Curve {
    public DoublePoint[] c;
    public String[] tag;
    public DoublePoint[] vertex;
    public double[] alpha0;
    public double[] alpha;
    public double[] beta;
    public int alphaCurve;

    public int n;

    public Curve(int n) {
        this.n = n;
        vertex = new DoublePoint[n];
        alpha0 = new double[n];
        tag = new String[n];
        c = new DoublePoint[n * 3];
        alpha = new double[n];
        beta = new double[n];
        alphaCurve = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Alpha[");
        for (double d : alpha)
            sb.append(d + ", ");
        sb.append("]");

        sb.append("\n Alpha0[");
        for (double d : alpha0)
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
        for (String t : tag)
            sb.append(t + ", ");
        sb.append("]");

        sb.append("\n c");
        sb.append("[");
        for (DoublePoint t : c)
            sb.append(t + ", ");
        sb.append("]");

        return sb.toString();
    }
}
