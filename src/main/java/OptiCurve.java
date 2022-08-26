public class OptiCurve {
    private Path path;
    private Info info;

    public OptiCurve(Path path, Info info) {
        this.path = path;
        this.info = info;
    }

    public int opti_penalty(Path path, int i, int j, Opti res, double opttolerance, int[] convc, double[] areac) {
        Curve curve = path.curve;
        DoublePoint[] vertex = curve.vertex;
        int m = curve.n;

        if (i==j) {
            return 1;
        }

        int k = i;
        int i1 = ProcessPath.mod(i+1, m);
        int k1 = ProcessPath.mod(k+1, m);
        int conv = convc[k1];
        if (conv == 0) {
            return 1;
        }
        double d = ProcessPath.ddist(vertex[i], vertex[i1]);
        for (k=k1; k!=j; k=k1) {
            k1 = ProcessPath.mod(k+1, m);
            int k2 = ProcessPath.mod(k+2, m);
            if (convc[k1] != conv) {
                return 1;
            }
            if (ProcessPath.sign(ProcessPath.cprod(vertex[i], vertex[i1], vertex[k1], vertex[k2])) !=
                    conv) {
                return 1;
            }
            if (ProcessPath.iprod1(vertex[i], vertex[i1], vertex[k1], vertex[k2]) <
                    d * ProcessPath.ddist(vertex[k1], vertex[k2]) * -0.999847695156) {
                return 1;
            }
        }

        DoublePoint p0 = curve.c[ProcessPath.mod(i,m) * 3 + 2].copy();
        DoublePoint p1 = vertex[ProcessPath.mod(i+1,m)].copy();
        DoublePoint p2 = vertex[ProcessPath.mod(j,m)].copy();
        DoublePoint p3 = curve.c[ProcessPath.mod(j,m) * 3 + 2].copy();

        double area = areac[j] - areac[i];
        area -= ProcessPath.dpara(vertex[0], curve.c[i * 3 + 2], curve.c[j * 3 + 2])/2;
        if (i>=j) {
            area += areac[m];
        }

        double A1 = ProcessPath.dpara(p0, p1, p2);
        double A2 = ProcessPath.dpara(p0, p1, p3);
        double A3 = ProcessPath.dpara(p0, p2, p3);

        double A4 = A1+A3-A2;

        if (A2 == A1) {
            return 1;
        }

        double t = A3/(A3-A4);
        double s = A2/(A2-A1);
        double A = A2 * t / 2.0;

        if (A == 0.0) {
            return 1;
        }

        double R = area / A;
        double alpha = 2 - Math.sqrt(4 - R / 0.3);

        res.c[0] = ProcessPath.interval(t * alpha, p0, p1);
        res.c[1] = ProcessPath.interval(s * alpha, p3, p2);
        res.alpha = alpha;
        res.t = t;
        res.s = s;

        p1 = res.c[0].copy();
        p2 = res.c[1].copy();

        res.pen = 0;

        for (k=ProcessPath.mod(i+1,m); k!=j; k=k1) {
            k1 = ProcessPath.mod(k+1,m);
            t = ProcessPath.tangent(p0, p1, p2, p3, vertex[k], vertex[k1]);
            if (t<-0.5) {
                return 1;
            }
            DoublePoint pt = ProcessPath.bezier(t, p0, p1, p2, p3);
            d = ProcessPath.ddist(vertex[k], vertex[k1]);
            if (d == 0.0) {
                return 1;
            }
            double d1 = ProcessPath.dpara(vertex[k], vertex[k1], pt) / d;
            if (Math.abs(d1) > opttolerance) {
                return 1;
            }
            if (ProcessPath.iprod(vertex[k], vertex[k1], pt) < 0 ||
                    ProcessPath.iprod(vertex[k1], vertex[k], pt) < 0) {
                return 1;
            }
            res.pen += d1 * d1;
        }

        for (k=i; k!=j; k=k1) {
            k1 = ProcessPath.mod(k+1,m);
            t = ProcessPath.tangent(p0, p1, p2, p3, curve.c[k * 3 + 2], curve.c[k1 * 3 + 2]);
            if (t<-0.5) {
                return 1;
            }
            DoublePoint pt = ProcessPath.bezier(t, p0, p1, p2, p3);
            d = ProcessPath.ddist(curve.c[k * 3 + 2], curve.c[k1 * 3 + 2]);
            if (d == 0.0) {
                return 1;
            }
            double d1 = ProcessPath.dpara(curve.c[k * 3 + 2], curve.c[k1 * 3 + 2], pt) / d;
            double d2 = ProcessPath.dpara(curve.c[k * 3 + 2], curve.c[k1 * 3 + 2], vertex[k1]) / d;
            d2 *= 0.75 * curve.alpha[k1];
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

    public void optiCurve() {
        Curve curve = path.curve;
        int m = curve.n;
        DoublePoint[] vert = curve.vertex;

        int[] pt = new int[m + 1];
        double[] pen = new double[m + 1];
        int[] len = new int[m + 1];
        Opti[] opt = new Opti[m + 1];
        Opti o = new Opti();

        int[] convc = new int[m];
        double[] areac = new double[m + 1];

        for (int i=0; i<m; i++) {
            if (curve.tag[i] == "CURVE") {
                convc[i] = ProcessPath.sign(ProcessPath.dpara(vert[ProcessPath.mod(i-1,m)], vert[i], vert[ProcessPath.mod(i+1,m)]));
            } else {
                convc[i] = 0;
            }
        }

        double area = 0.0;
        areac[0] = 0.0;
        DoublePoint p0 = curve.vertex[0];
        for (int i=0; i<m; i++) {
            int i1 = ProcessPath.mod(i+1, m);
            if (curve.tag[i1] == "CURVE") {
                double alpha = curve.alpha[i1];
                area += 0.3 * alpha * (4-alpha) *
                        ProcessPath.dpara(curve.c[i * 3 + 2], vert[i1], curve.c[i1 * 3 + 2])/2;
                area += ProcessPath.dpara(p0, curve.c[i * 3 + 2], curve.c[i1 * 3 + 2])/2;
            }
            areac[i+1] = area;
        }

        pt[0] = -1;
        pen[0] = 0;
        len[0] = 0;


        for (int j=1; j<=m; j++) {
            pt[j] = j-1;
            pen[j] = pen[j-1];
            len[j] = len[j-1]+1;

            for (int i=j-2; i>=0; i--) {
                int r = opti_penalty(path, i, ProcessPath.mod(j,m), o, info.opttolerance, convc,
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
        }
        int om = len[m];
        Curve ocurve = new Curve(om);
        double[] s = new double[om];
        double[] t = new double[om];

        int j = m;
        for (int i=om-1; i>=0; i--) {
            if (pt[j]==j-1) {
                ocurve.tag[i]     = curve.tag[ProcessPath.mod(j,m)];
                ocurve.c[i * 3 + 0]    = curve.c[ProcessPath.mod(j,m) * 3 + 0];
                ocurve.c[i * 3 + 1]    = curve.c[ProcessPath.mod(j,m) * 3 + 1];
                ocurve.c[i * 3 + 2]    = curve.c[ProcessPath.mod(j,m) * 3 + 2];
                ocurve.vertex[i]  = curve.vertex[ProcessPath.mod(j,m)];
                ocurve.alpha[i]   = curve.alpha[ProcessPath.mod(j,m)];
                ocurve.alpha0[i]  = curve.alpha0[ProcessPath.mod(j,m)];
                ocurve.beta[i]    = curve.beta[ProcessPath.mod(j,m)];
                s[i] = t[i] = 1.0;
            } else {
                ocurve.tag[i] = "CURVE";
                ocurve.c[i * 3 + 0] = opt[j].c[0];
                ocurve.c[i * 3 + 1] = opt[j].c[1];
                ocurve.c[i * 3 + 2] = curve.c[ProcessPath.mod(j,m) * 3 + 2];
                ocurve.vertex[i] = ProcessPath.interval(opt[j].s, curve.c[ProcessPath.mod(j,m) * 3 + 2],
                        vert[ProcessPath.mod(j,m)]);
                ocurve.alpha[i] = opt[j].alpha;
                ocurve.alpha0[i] = opt[j].alpha;
                s[i] = opt[j].s;
                t[i] = opt[j].t;
            }
            j = pt[j];
        }

        for (int i=0; i<om; i++) {
            int i1 = ProcessPath.mod(i+1,om);
            ocurve.beta[i] = s[i] / (s[i] + t[i1]);
        }
        ocurve.alphaCurve = 1;
        path.curve = ocurve;
    }
}
