package tracing.base;

import geometry.*;
import utils.MathUtils;

import java.util.List;

public class ProcessPath {
    private final Settings settings;
    private final List<Path> pathList;

    public ProcessPath(Settings settings, List<Path> pathList) {
        this.settings = settings;
        this.pathList = pathList;
    }

    private boolean cyclic(double a, double b, double c) {
        if (a <= c) {
            return (a <= b && b < c);
        } else {
            return (a <= b || b < c);
        }
    }

    private DoublePoint nextDirection(DoublePoint p0, DoublePoint p2) {
        DoublePoint r = new DoublePoint();

        r.setY(MathUtils.sign(p2.getX() - p0.getX()));
        r.setX(-MathUtils.sign(p2.getY() - p0.getY()));

        return r;
    }

    private double ddenom(DoublePoint p0, DoublePoint p2) {
        DoublePoint r = nextDirection(p0, p2);

        return r.getY() * (p2.getX() - p0.getX()) - r.getX() * (p2.getY() - p0.getY());
    }

    private void calcSums(Path path) {
        IntegerPoint firstPoint = path.getFirstPoint();

        List<Sum> s = path.getSums();
        s.add(new Sum(0, 0, 0, 0, 0));
        for(int i = 0; i < path.getLen(); i++){
            int x = path.getPoints().get(i).getX() - firstPoint.getX();
            int y = path.getPoints().get(i).getY() - firstPoint.getY();
            s.add(new Sum(s.get(i).x + x, s.get(i).y + y, s.get(i).xy + x * y,
                    s.get(i).x2 + x * x, s.get(i).y2 + y * y));
        }
    }

    private int[] initializeNextCorner(Path path) {
        List<IntegerPoint> pt = path.getPoints();
        int n = path.getLen();
        int[] nextCorner = new int[n];
        int k = 0;
        for(int i = n - 1; i >= 0; i--){
            if (pt.get(i).getX() != pt.get(k).getX() && pt.get(i).getY() != pt.get(k).getY()) {
                k = i + 1;
            }
            nextCorner[i] = k;
        }
        return nextCorner;
    }

    private void updateConstraint(DoublePoint cur, DoublePoint[] constraint) {
        DoublePoint off = new DoublePoint();
        off.setX(cur.getX() + ((cur.getY() >= 0 && (cur.getY() > 0 || cur.getX() < 0)) ? 1 : -1));
        off.setY(cur.getY() + ((cur.getX() <= 0 && (cur.getX() < 0 || cur.getY() < 0)) ? 1 : -1));
        if (MathUtils.crossProduct(constraint[0], off) >= 0) {
            constraint[0].setX(off.getX());
            constraint[0].setY(off.getY());
        }
        off.setX(cur.getX() + ((cur.getY() <= 0 && (cur.getY() < 0 || cur.getX() < 0)) ? 1 : -1));
        off.setY(cur.getY() + ((cur.getX() >= 0 && (cur.getX() > 0 || cur.getY() < 0)) ? 1 : -1));
        if (MathUtils.crossProduct(constraint[1], off) <= 0) {
            constraint[1].setX(off.getX());
            constraint[1].setY(off.getY());
        }
    }

    private void cleanUp(Path path, int[] pivk) {
        int n = path.getLen();
        int j=pivk[n-1];
        path.getLongestStraightLine()[n-1]=j;
        for (int i=n-2; i>=0; i--) {
            if (cyclic(i+1d,pivk[i],j)) {
                j=pivk[i];
            }
            path.getLongestStraightLine()[i]=j;
        }
        for (int i = n-1; cyclic(MathUtils.mod(i+1,n),j,path.getLongestStraightLine()[i]); i--) {
            path.getLongestStraightLine()[i] = j;
        }
    }

    private int findLastAcceptablePoint(int k1, DoublePoint cur, DoublePoint dk, Path path, DoublePoint[] constraint) {
        List<IntegerPoint> pt = path.getPoints();
        int n = pt.size();

        double a = MathUtils.crossProduct(constraint[0], cur);
        double b = MathUtils.crossProduct(constraint[0], dk);
        double c = MathUtils.crossProduct(constraint[1], cur);
        double d = MathUtils.crossProduct(constraint[1], dk);

        int j = 10000000;
        if (b < 0) {
            j = (int)Math.floor(a / -b);
        }
        if (d > 0) {
            j = Math.min(j, (int)Math.floor(-c / d));
        }
        return  MathUtils.mod(k1+j,n);
    }

    private boolean isAcceptable(DoublePoint cur, DoublePoint[] constraint) {
        return !(MathUtils.crossProduct(constraint[0], cur) < 0 || MathUtils.crossProduct(constraint[1], cur) > 0);
    }

    private boolean isAllDirectionOccurred(double[] ct) {
        return ct[0] != 0 && ct[1] != 0 && ct[2] != 0 && ct[3] != 0;
    }

    private void generatePivotPoints(int i, Path path, int[] nextCorner, int[] pivk) {
        DoublePoint[] constraint = {new DoublePoint(), new DoublePoint()};
        DoublePoint cur = new DoublePoint();
        List<IntegerPoint> pt = path.getPoints();
        int n = pt.size();
        double[] ct = new double[4];
        ct[0] = ct[1] = ct[2] = ct[3] = 0;
        int dir = (3 + 3 * (pt.get(MathUtils.mod(i + 1, n)).getX() - pt.get(i).getX()) +
                (pt.get(MathUtils.mod(i + 1, n)).getY() - pt.get(i).getY())) / 2;
        ct[dir]++;

        constraint[0].setX(0);
        constraint[0].setY(0);
        constraint[1].setX(0);
        constraint[1].setY(0);

        int k = nextCorner[i];
        int k1 = i;
        boolean foundk;
        while (true) {
            foundk = false;
            dir =  (3 + 3 * MathUtils.sign((double) pt.get(k).getX() - pt.get(k1).getX()) +
                    MathUtils.sign((double) pt.get(k).getY() - pt.get(k1).getY())) / 2;
            ct[dir]++;

            if (isAllDirectionOccurred(ct)) {
                pivk[i] = k1;
                foundk = true;
                break;
            }

            cur.setX((double) pt.get(k).getX() - pt.get(i).getX());
            cur.setY((double) pt.get(k).getY() - pt.get(i).getY());

            if (!isAcceptable(cur, constraint)) {
                break;
            }

            if (Math.abs(cur.getX()) > 1 || Math.abs(cur.getY()) > 1) {
                updateConstraint(cur, constraint);
            }
            k1 = k;
            k = nextCorner[k1];
            if (!cyclic(k, i, k1)) {
                break;
            }
        }
        if (!foundk) {
            DoublePoint dk = new DoublePoint();
            dk.setX(MathUtils.sign((double) pt.get(k).getX()-pt.get(k1).getX()));
            dk.setY(MathUtils.sign((double) pt.get(k).getY()-pt.get(k1).getY()));
            cur.setX((double) pt.get(k1).getX() - pt.get(i).getX());
            cur.setY((double) pt.get(k1).getY() - pt.get(i).getY());
            pivk[i] = findLastAcceptablePoint(k1, cur, dk, path, constraint);
        }
    }

    private void findStraightPaths(Path path) {
        int n = path.getLen();
        int[] pivk = new int[n];
        path.setLongestStraightLine(new int[n]);
        int[] nextCorner = initializeNextCorner(path);

        for (int i = n - 1; i >= 0; i--) {
            generatePivotPoints(i, path, nextCorner, pivk);
        }
        cleanUp(path, pivk);
    }

    private void reverse(Path path) {
        Curve curve = path.getCurve();
        int m = curve.getVertex().length;
        DoublePoint[] v = curve.getVertex();

        for (int i=0, j=m-1; i<j; i++, j--) {
            DoublePoint tmp = v[i];
            v[i] = v[j];
            v[j] = tmp;
        }
    }

    private void smooth(Path path) {
        Curve curve = path.getCurve();
        int m = curve.getVertex().length;

        double alpha;
        DoublePoint p2;
        DoublePoint p3;
        DoublePoint p4;

        for (int i=0; i<m; i++) {
            int j = MathUtils.mod(i+1, m);
            int k = MathUtils.mod(i+2, m);
            p4 = MathUtils.interval(1/2.0, curve.getVertex()[k], curve.getVertex()[j]);

            double denom = ddenom(curve.getVertex()[i], curve.getVertex()[k]);
            if (denom != 0.0) {
                double dd = MathUtils.parallelogramArea(curve.getVertex()[i], curve.getVertex()[j], curve.getVertex()[k]) / denom;
                dd = Math.abs(dd);
                alpha = dd > 1 ? (1 - 1.0/dd) : 0;
                alpha = alpha / 0.75;
            } else {
                alpha = 4/3.0;
            }

            if (alpha >= settings.getAlphaMax()) {
                curve.getTag()[j] = Tag.CORNER;
                curve.getControlPoints()[3 * j + 1] = curve.getVertex()[j];
                curve.getControlPoints()[3 * j + 2] = p4;
            } else {
                if (alpha < 0.55) {
                    alpha = 0.55;
                } else if (alpha > 1) {
                    alpha = 1;
                }
                p2 = MathUtils.interval(0.5+0.5*alpha, curve.getVertex()[i], curve.getVertex()[j]);
                p3 = MathUtils.interval(0.5+0.5*alpha, curve.getVertex()[k], curve.getVertex()[j]);
                curve.getTag()[j] = Tag.CURVE;
                curve.getControlPoints()[3 * j] = p2;
                curve.getControlPoints()[3 * j + 1] = p3;
                curve.getControlPoints()[3 * j + 2] = p4;
            }
            curve.getAlpha()[j] = alpha;
            curve.getBeta()[j] = 0.5;
        }
        curve.setIsInitializated(true);
    }

    public void processPath() {
        for (Path path : pathList) {
            calcSums(path);
            findStraightPaths(path);

            BestPolygon bestPolygon = new BestPolygon(path);
            bestPolygon.bestPolygon();

            AdjustVertices adjustVertices = new AdjustVertices(path);
            adjustVertices.adjustVertices();

            if (path.getSign().equals(Path.Sign.MINUS)) {
                reverse(path);
            }

            smooth(path);

            if (settings.isOptimizeCurve()) {
                OptiCurve optiCurve = new OptiCurve(path, settings);
                optiCurve.optiCurve();
            }
        }
    }

}
