package utils;

import geometry.DoublePoint;
import potrace.Quad;

public class MathUtils {
    private MathUtils() {}

    public static double square(double x) {
        return x * x;
    }

    public static int mod(int a, int n) {
        if (a >= n)
            return a % n;
        if (a >= 0)
            return a;
        return n-1-(-1-a) % n;
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


    public static double parallelogramArea(DoublePoint p0, DoublePoint p1, DoublePoint p2) {
        double x1 = p1.getX() - p0.getX();
        double y1 = p1.getY() - p0.getY();
        double x2 = p2.getX() - p0.getX();
        double y2 = p2.getY() - p0.getY();

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

    public static double crossProduct(DoublePoint p1, DoublePoint p2) {
        return p1.getX() * p2.getY() - p1.getY() * p2.getX();
    }
}
