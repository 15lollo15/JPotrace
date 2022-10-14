package tracing.base;

import geometry.DoublePoint;

public class Opti {
    private double pen = 0;
    private DoublePoint[] c = {new DoublePoint(), new DoublePoint()};
    private double t;
    private double s;
    private double alpha;

    public double getPen() {
        return pen;
    }

    public void setPen(double pen) {
        this.pen = pen;
    }

    public DoublePoint[] getC() {
        return c;
    }

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    public double getS() {
        return s;
    }

    public void setS(double s) {
        this.s = s;
    }

    public void setC(DoublePoint[] c) {
        this.c = c;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }
}
