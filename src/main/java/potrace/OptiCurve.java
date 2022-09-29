package potrace;

import geometry.Curve;
import geometry.DoublePoint;
import geometry.Path;
import geometry.Tag;
import utils.MathUtils;

public class OptiCurve {
    private final Path path;
    private final Settings settings;

    public OptiCurve(Path path, Settings settings) {
        this.path = path;
        this.settings = settings;
    }

    private int optiPenalty(Path path, int i, int j, Opti res, double opttolerance, int[] convc, double[] areac) {
        Curve curve = path.getCurve();
        DoublePoint[] c = curve.getControlPoints();
        DoublePoint[] vertex = curve.getVertex();
        int m = vertex.length;

        if (i==j) {
            return 1;
        }

        int k = i;
        int i1 = MathUtils.mod(i+1, m);
        int k1 = MathUtils.mod(k+1, m);
        int conv = convc[k1];
        if (conv == 0) {
            return 1;
        }
        double d = MathUtils.ddist(vertex[i], vertex[i1]);
        for (k=k1; k!=j; k=k1) {
            k1 = MathUtils.mod(k+1, m);
            int k2 = MathUtils.mod(k+2, m);
            if (convc[k1] != conv) {
                return 1;
            }
            if (MathUtils.sign(MathUtils.cprod(vertex[i], vertex[i1], vertex[k1], vertex[k2])) !=
                    conv) {
                return 1;
            }
            if (MathUtils.iprod1(vertex[i], vertex[i1], vertex[k1], vertex[k2]) <
                    d * MathUtils.ddist(vertex[k1], vertex[k2]) * -0.999847695156) {
                return 1;
            }
        }

        DoublePoint p0 = c[MathUtils.mod(i,m) * 3 + 2].copy();
        DoublePoint p1 = vertex[MathUtils.mod(i+1,m)].copy();
        DoublePoint p2 = vertex[MathUtils.mod(j,m)].copy();
        DoublePoint p3 = c[MathUtils.mod(j,m) * 3 + 2].copy();

        double area = areac[j] - areac[i];
        area -= MathUtils.parallelogramArea(vertex[0], c[i * 3 + 2], c[j * 3 + 2])/2;
        if (i>=j) {
            area += areac[m];
        }

        double a1 = MathUtils.parallelogramArea(p0, p1, p2);
        double a2 = MathUtils.parallelogramArea(p0, p1, p3);
        double a3 = MathUtils.parallelogramArea(p0, p2, p3);

        double a4 = a1+a3-a2;

        if (a2 == a1) {
            return 1;
        }

        double t = a3/(a3-a4);
        double s = a2/(a2-a1);
        double a = a2 * t / 2.0;

        if (a == 0.0) {
            return 1;
        }

        double r = area / a;
        double alpha = 2 - Math.sqrt(4 - r / 0.3);

        res.c[0] = MathUtils.interval(t * alpha, p0, p1);
        res.c[1] = MathUtils.interval(s * alpha, p3, p2);
        res.alpha = alpha;
        res.t = t;
        res.s = s;

        p1 = res.c[0].copy();
        p2 = res.c[1].copy();

        res.pen = 0;

        for (k=MathUtils.mod(i+1,m); k!=j; k=k1) {
            k1 = MathUtils.mod(k+1,m);
            t = MathUtils.tangent(p0, p1, p2, p3, vertex[k], vertex[k1]);
            if (t<-0.5) {
                return 1;
            }
            DoublePoint pt = MathUtils.bezier(t, p0, p1, p2, p3);
            d = MathUtils.ddist(vertex[k], vertex[k1]);
            if (d == 0.0) {
                return 1;
            }
            double d1 = MathUtils.parallelogramArea(vertex[k], vertex[k1], pt) / d;
            if (Math.abs(d1) > opttolerance) {
                return 1;
            }
            if (MathUtils.iprod(vertex[k], vertex[k1], pt) < 0 ||
                    MathUtils.iprod(vertex[k1], vertex[k], pt) < 0) {
                return 1;
            }
            res.pen += d1 * d1;
        }

        for (k=i; k!=j; k=k1) {
            k1 = MathUtils.mod(k+1,m);
            t = MathUtils.tangent(p0, p1, p2, p3, c[k * 3 + 2], c[k1 * 3 + 2]);
            if (t<-0.5) {
                return 1;
            }
            DoublePoint pt = MathUtils.bezier(t, p0, p1, p2, p3);
            d = MathUtils.ddist(c[k * 3 + 2], c[k1 * 3 + 2]);
            if (d == 0.0) {
                return 1;
            }
            double d1 = MathUtils.parallelogramArea(c[k * 3 + 2], c[k1 * 3 + 2], pt) / d;
            double d2 = MathUtils.parallelogramArea(c[k * 3 + 2], c[k1 * 3 + 2], vertex[k1]) / d;
            d2 *= 0.75 * curve.getAlpha()[k1];
            if (d2 < 0) {
                d1 = -d1;
                d2 = -d2;
            }
            if (d1 < d2 - opttolerance) {
                return 1;
            }
            if (d1 < d2) {
                res.pen += (d1 - d2) * (d1 - d2);
            }
        }

        return 0;
    }

    private double[] computeAreas(Curve curve) {
        int m = curve.getVertex().length;
        DoublePoint[] c = curve.getControlPoints();
        double[] areac = new double[m + 1];
        DoublePoint[] vert = curve.getVertex();
        double area = 0.0;
        areac[0] = 0.0;
        DoublePoint p0 = curve.getVertex()[0];
        for (int i=0; i<m; i++) {
            int i1 = MathUtils.mod(i+1, m);
            if (curve.getTag()[i1] == Tag.CURVE) {
                double alpha = curve.getAlpha()[i1];
                area += 0.3 * alpha * (4-alpha) *
                        MathUtils.parallelogramArea(c[i * 3 + 2], vert[i1], c[i1 * 3 + 2])/2;
                area += MathUtils.parallelogramArea(p0, c[i * 3 + 2], c[i1 * 3 + 2])/2;
            }
            areac[i+1] = area;
        }
        return areac;
    }

    private int[] computeConvexity(Curve curve) {
        int m = curve.getVertex().length;
        DoublePoint[] vert = curve.getVertex();
        int[] convc = new int[m];
        for (int i=0; i<m; i++) {
            if (curve.getTag()[i] == Tag.CURVE) {
                convc[i] = MathUtils.sign(MathUtils.parallelogramArea(vert[MathUtils.mod(i-1,m)], vert[i], vert[MathUtils.mod(i+1,m)]));
            } else {
                convc[i] = 0;
            }
        }
        return convc;
    }

    public Opti bestPath(int j, int[] pt, double[] pen, int[] len, int m, int[] convc, double[] areac) {

        pt[j] = j-1;
        pen[j] = pen[j-1];
        len[j] = len[j-1]+1;

        Opti o = new Opti();
        Opti[] opt = new Opti[m + 1];
        for (int i=j-2; i>=0; i--) {
            int r = optiPenalty(path, i, MathUtils.mod(j,m), o, settings.getOptimalityTolerance(), convc,
                    areac);
            if (r != 0) {
                break;
            }
            if (len[j] > len[i]+1 ||
                    (len[j] == len[i]+1 && pen[j] > pen[i] + o.pen)) {
                pt[j] = i;
                pen[j] = pen[i] + o.pen;
                len[j] = len[i] + 1;
                opt[j] = o;
                o = new Opti();
            }
        }
        return opt[j];
    }

    public void optiCurve() {
        Curve curve = path.getCurve();
        DoublePoint[] c = curve.getControlPoints();
        int m = curve.getVertex().length;
        DoublePoint[] vert = curve.getVertex();

        int[] pt = new int[m + 1];
        double[] pen = new double[m + 1];
        int[] len = new int[m + 1];
        Opti[] opt = new Opti[m + 1];

        int[] convc = computeConvexity(curve);
        double[] areac = computeAreas(curve);

        pt[0] = -1;
        pen[0] = 0;
        len[0] = 0;


        for (int j=1; j<=m; j++) {
            opt[j] = bestPath(j, pt, pen, len, m, convc, areac);
        }
        int om = len[m];
        Curve ocurve = new Curve(om);
        double[] s = new double[om];
        double[] t = new double[om];

        int j = m;
        DoublePoint[] oc = ocurve.getControlPoints();
        for (int i=om-1; i>=0; i--) {
            if (pt[j]==j-1) {
                ocurve.getTag()[i]     = curve.getTag()[MathUtils.mod(j,m)];
                oc[i * 3]    = c[MathUtils.mod(j,m) * 3];
                oc[i * 3 + 1]    = c[MathUtils.mod(j,m) * 3 + 1];
                oc[i * 3 + 2]    = c[MathUtils.mod(j,m) * 3 + 2];
                ocurve.getVertex()[i]  = curve.getVertex()[MathUtils.mod(j,m)];
                ocurve.getAlpha()[i]   = curve.getAlpha()[MathUtils.mod(j,m)];
                ocurve.getBeta()[i]    = curve.getBeta()[MathUtils.mod(j,m)];
                s[i] = t[i] = 1.0;
            } else {
                ocurve.getTag()[i] = Tag.CURVE;
                oc[i * 3] = opt[j].c[0];
                oc[i * 3 + 1] = opt[j].c[1];
                oc[i * 3 + 2] = c[MathUtils.mod(j,m) * 3 + 2];
                ocurve.getVertex()[i] = MathUtils.interval(opt[j].s, c[MathUtils.mod(j,m) * 3 + 2],
                        vert[MathUtils.mod(j,m)]);
                ocurve.getAlpha()[i] = opt[j].alpha;
                s[i] = opt[j].s;
                t[i] = opt[j].t;
            }
            j = pt[j];
        }

        for (int i=0; i<om; i++) {
            int i1 = MathUtils.mod(i+1,om);
            ocurve.getBeta()[i] = s[i] / (s[i] + t[i1]);
        }
        ocurve.setIsInitializated(true);
        path.setCurve(ocurve);
    }
}
