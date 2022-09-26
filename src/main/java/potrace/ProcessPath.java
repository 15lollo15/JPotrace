package potrace;

import geometry.*;

import java.util.ArrayList;
import java.util.List;

public class ProcessPath {
    private Info info;
    private List<Path> pathlist;

    public ProcessPath(Info info, List<Path> pathlist) {
        this.info = info;
        this.pathlist = pathlist;
    }

    public static int mod(int a, int n) {
        if (a >= n)
            return a % n;
        if (a >= 0)
            return a;
        return n-1-(-1-a) % n;
    }

    public double xprod(DoublePoint p1, DoublePoint p2) {
        return p1.getX() * p2.getY() - p1.getY() * p2.getX();
    }

    public boolean cyclic(double a, double b, double c) {
        if (a <= c) {
            return (a <= b && b < c);
        } else {
            return (a <= b || b < c);
        }
    }

    public static int sign(double i) {
        if (i > 0)
            return 1;
        if (i < 0)
            return -1;
        return 0;
    }

    public static double quadform(Quad quad, DoublePoint w) {
        double[] v = new double[3];

        v[0] = w.getX();
        v[1] = w.getY();
        v[2] = 1;
        double sum = 0.0;

        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                sum += v[i] * quad.at(i, j) * v[j];
            }
        }
        return sum;
    }

    public static DoublePoint interval(double lambda, DoublePoint a, DoublePoint b) {
        DoublePoint res = new DoublePoint();

        res.setX(a.getX() + lambda * (b.getX() - a.getX()));
        res.setY(a.getY() + lambda * (b.getY() - a.getY()));
        return res;
    }

    public DoublePoint dorthInfty(DoublePoint p0, DoublePoint p2) {
        DoublePoint r = new DoublePoint();

        r.setY(sign(p2.getX() - p0.getX()));
        r.setX(-sign(p2.getY() - p0.getY()));

        return r;
    }

    public double ddenom(DoublePoint p0, DoublePoint p2) {
        DoublePoint r = dorthInfty(p0, p2);

        return r.getY() * (p2.getX() - p0.getX()) - r.getX() * (p2.getY() - p0.getY());
    }

    public static double dpara(DoublePoint p0, DoublePoint p1, DoublePoint p2) {
        double x1, y1, x2, y2;

        x1 = p1.getX() - p0.getX();
        y1 = p1.getY() - p0.getY();
        x2 = p2.getX() - p0.getX();
        y2 = p2.getY() - p0.getY();

        return x1 * y2 - x2 * y1;
    }

    public static double cprod(DoublePoint p0, DoublePoint p1, DoublePoint p2, DoublePoint p3) {
        double x1, y1, x2, y2;

        x1 = p1.getX() - p0.getX();
        y1 = p1.getY() - p0.getY();
        x2 = p3.getX() - p2.getX();
        y2 = p3.getY() - p2.getY();

        return x1 * y2 - x2 * y1;
    }

    public static double iprod(DoublePoint p0, DoublePoint p1, DoublePoint p2) {
        double x1, y1, x2, y2;

        x1 = p1.getX() - p0.getX();
        y1 = p1.getY() - p0.getY();
        x2 = p2.getX() - p0.getX();
        y2 = p2.getY() - p0.getY();

        return x1*x2 + y1*y2;
    }

    public static double iprod1(DoublePoint p0, DoublePoint p1, DoublePoint p2, DoublePoint p3) {
        double x1, y1, x2, y2;

        x1 = p1.getX() - p0.getX();
        y1 = p1.getY() - p0.getY();
        x2 = p3.getX() - p2.getX();
        y2 = p3.getY() - p2.getY();

        return x1 * x2 + y1 * y2;
    }

    public static double ddist(DoublePoint p, DoublePoint q) {
        return Math.sqrt((p.getX() - q.getX()) * (p.getX() - q.getX()) + (p.getY() - q.getY()) * (p.getY() - q.getY()));
    }

    public static DoublePoint bezier(double t, DoublePoint p0, DoublePoint p1, DoublePoint p2, DoublePoint p3) {
        double s = 1 - t;
        DoublePoint res = new DoublePoint();

        res.setX(s*s*s*p0.getX() + 3*(s*s*t)*p1.getX() + 3*(t*t*s)*p2.getX() + t*t*t*p3.getX());
        res.setY(s*s*s*p0.getY() + 3*(s*s*t)*p1.getY() + 3*(t*t*s)*p2.getY() + t*t*t*p3.getY());

        return res;
    }

    public static double tangent(DoublePoint p0, DoublePoint p1, DoublePoint p2, DoublePoint p3, DoublePoint q0, DoublePoint q1) {
        double A, B, C, a, b, c, d, s, r1, r2;

        A = cprod(p0, p1, q0, q1);
        B = cprod(p1, p2, q0, q1);
        C = cprod(p2, p3, q0, q1);

        a = A - 2 * B + C;
        b = -2 * A + 2 * B;
        c = A;

        d = b * b - 4 * a * c;

        if (a==0 || d<0) {
            return -1.0;
        }

        s = Math.sqrt(d);

        r1 = (-b + s) / (2 * a);
        r2 = (-b - s) / (2 * a);

        if (r1 >= 0 && r1 <= 1) {
            return r1;
        } else if (r2 >= 0 && r2 <= 1) {
            return r2;
        } else {
            return -1.0;
        }
    }

    public void calcSums(Path path) {
        path.x0 = path.pt.get(0).getX();
        path.y0 = path.pt.get(0).getY();

        path.sums = new ArrayList<>();
        List<Sum> s = path.sums;
        s.add(new Sum(0, 0, 0, 0, 0));
        for(int i = 0; i < path.len; i++){
            int x = path.pt.get(i).getX() - path.x0;
            int y = path.pt.get(i).getY() - path.y0;
            s.add(new Sum(s.get(i).x + x, s.get(i).y + y, s.get(i).xy + x * y,
                    s.get(i).x2 + x * x, s.get(i).y2 + y * y));
        }
    }

    public void  calcLon(Path path) {
        int n = path.len;
        List<IntegerPoint> pt = path.pt;
        int[] nc = new int[n];
        double[] ct = new double[4];
        int dir;
        int[] pivk = new int[n];
        path.lon = new int[n];

        DoublePoint[] constraint = {new DoublePoint(), new DoublePoint()};
        DoublePoint cur = new DoublePoint();
        DoublePoint off = new DoublePoint();
        DoublePoint dk = new DoublePoint();
        int foundk;
        int j;

        double a, b, c, d;
        int k = 0;
        int k1;
        for(int i = n - 1; i >= 0; i--){
            if (pt.get(i).getX() != pt.get(k).getX() && pt.get(i).getY() != pt.get(k).getY()) {
                k = i + 1;
            }
            nc[i] = k;
        }

        for (int i = n - 1; i >= 0; i--) {
            ct[0] = ct[1] = ct[2] = ct[3] = 0;
            dir = (3 + 3 * (pt.get(mod(i + 1, n)).getX() - pt.get(i).getX()) +
                    (pt.get(mod(i + 1, n)).getY() - pt.get(i).getY())) / 2;
            ct[dir]++;

            constraint[0].setX(0);
            constraint[0].setY(0);
            constraint[1].setX(0);
            constraint[1].setY(0);

            k = nc[i];
            k1 = i;
            while (true) {
                foundk = 0;
                dir =  (3 + 3 * sign(pt.get(k).getX() - pt.get(k1).getX()) +
                        sign(pt.get(k).getY() - pt.get(k1).getY())) / 2;
                ct[dir]++;

                if (ct[0] != 0 && ct[1] != 0 && ct[2] != 0 && ct[3] != 0 ) {
                    pivk[i] = k1;
                    foundk = 1;
                    break;
                }

                cur.setX((double) (pt.get(k).getX() - pt.get(i).getX()));
                cur.setY((double) (pt.get(k).getY() - pt.get(i).getY()));

                if (xprod(constraint[0], cur) < 0 || xprod(constraint[1], cur) > 0) {
                    break;
                }

                if (Math.abs(cur.getX()) > 1 || Math.abs(cur.getY()) > 1) {
                    off.setX(cur.getX() + ((cur.getY() >= 0 && (cur.getY() > 0 || cur.getX() < 0)) ? 1 : -1));
                    off.setY(cur.getY() + ((cur.getX() <= 0 && (cur.getX() < 0 || cur.getY() < 0)) ? 1 : -1));
                    if (xprod(constraint[0], off) >= 0) {
                        constraint[0].setX(off.getX());
                        constraint[0].setY(off.getY());
                    }
                    off.setX(cur.getX() + ((cur.getY() <= 0 && (cur.getY() < 0 || cur.getX() < 0)) ? 1 : -1));
                    off.setY(cur.getY() + ((cur.getX() >= 0 && (cur.getX() > 0 || cur.getY() < 0)) ? 1 : -1));
                    if (xprod(constraint[1], off) <= 0) {
                        constraint[1].setX(off.getX());
                        constraint[1].setY(off.getY());
                    }
                }
                k1 = k;
                k = nc[k1];
                if (!cyclic(k, i, k1)) {
                    break;
                }
            }
            if (foundk == 0) {
                dk.setX((double) sign(pt.get(k).getX()-pt.get(k1).getX()));
                dk.setY((double) sign(pt.get(k).getY()-pt.get(k1).getY()));
                cur.setX((double) (pt.get(k1).getX() - pt.get(i).getX()));
                cur.setY((double) (pt.get(k1).getY() - pt.get(i).getY()));

                a = xprod(constraint[0], cur);
                b = xprod(constraint[0], dk);
                c = xprod(constraint[1], cur);
                d = xprod(constraint[1], dk);

                j = 10000000;
                if (b < 0) {
                    j = (int)Math.floor(a / -b);
                }
                if (d > 0) {
                    j = Math.min(j, (int)Math.floor(-c / d));
                }
                pivk[i] = mod(k1+j,n);
            }
        }

        j=pivk[n-1];
        path.lon[n-1]=j;
        for (int i=n-2; i>=0; i--) {
            if (cyclic(i+1,pivk[i],j)) {
                j=pivk[i];
            }
            path.lon[i]=j;
        }

        for (int i=n-1; cyclic(mod(i+1,n),j,path.lon[i]); i--) {
            path.lon[i] = j;
        }
    }

    public void reverse(Path path) {
        Curve curve = path.curve;
        int m = curve.getN();
        DoublePoint[] v = curve.getVertex();

        for (int i=0, j=m-1; i<j; i++, j--) {
            DoublePoint tmp = v[i];
            v[i] = v[j];
            v[j] = tmp;
        }
    }

    public void smooth(Path path) {
        Curve curve = path.curve;
        var m = path.curve.getN();

        double alpha;
        DoublePoint p2, p3, p4;

        for (int i=0; i<m; i++) {
            int j = mod(i+1, m);
            int k = mod(i+2, m);
            p4 = interval(1/2.0, curve.getVertex()[k], curve.getVertex()[j]);

            double denom = ddenom(curve.getVertex()[i], curve.getVertex()[k]);
            if (denom != 0.0) {
                double dd = dpara(curve.getVertex()[i], curve.getVertex()[j], curve.getVertex()[k]) / denom;
                dd = Math.abs(dd);
                alpha = dd>1 ? (1 - 1.0/dd) : 0;
                alpha = alpha / 0.75;
            } else {
                alpha = 4/3.0;
            }
            curve.getAlpha0()[j] = alpha;

            if (alpha >= info.alphamax) {
                curve.getTag()[j] = Tag.CORNER;
                curve.getC()[3 * j + 1] = curve.getVertex()[j];
                curve.getC()[3 * j + 2] = p4;
            } else {
                if (alpha < 0.55) {
                    alpha = 0.55;
                } else if (alpha > 1) {
                    alpha = 1;
                }
                p2 = interval(0.5+0.5*alpha, curve.getVertex()[i], curve.getVertex()[j]);
                p3 = interval(0.5+0.5*alpha, curve.getVertex()[k], curve.getVertex()[j]);
                curve.getTag()[j] = Tag.CURVE;
                curve.getC()[3 * j + 0] = p2;
                curve.getC()[3 * j + 1] = p3;
                curve.getC()[3 * j + 2] = p4;
            }
            curve.getAlpha()[j] = alpha;
            curve.getBeta()[j] = 0.5;
        }
        curve.setAlphaCurve(1);
    }

    public void processPath() {
        for (var i = 0; i < pathlist.size(); i++) {
            Path path = pathlist.get(i);
            calcSums(path);
            calcLon(path);

            BestPolygon bestPolygon = new BestPolygon(path);
            bestPolygon.bestPolygon();

            AdjustVertices adjustVertices = new AdjustVertices(path);
            adjustVertices.adjustVertices();

            if (path.sign.equals("-")) {
                reverse(path);
            }

            smooth(path);

            if (info.optcurve) {
                OptiCurve optiCurve = new OptiCurve(path, info);
                optiCurve.optiCurve();
            }
        }
    }

}
