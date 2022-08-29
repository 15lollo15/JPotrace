package potrace;

import geometry.Curve;
import geometry.DoublePoint;
import geometry.Path;
import geometry.Point;

import java.util.List;

public class AdjustVertices {
    private Path path;

    public AdjustVertices(Path path) {
        this.path = path;
    }

    public void pointslope(Path path, int i, int j, DoublePoint ctr, DoublePoint dir) {
        List<Sum> sums = path.sums;

        int n = path.len;
        int r = 0;
        double l;

        while (j>=n) {
            j-=n;
            r+=1;
        }
        while (i>=n) {
            i-=n;
            r-=1;
        }
        while (j<0) {
            j+=n;
            r-=1;
        }
        while (i<0) {
            i+=n;
            r+=1;
        }

        double x = sums.get(j+1).x-sums.get(i).x+r*sums.get(n).x;
        double y = sums.get(j+1).y-sums.get(i).y+r*sums.get(n).y;
        double x2 = sums.get(j+1).x2-sums.get(i).x2+r*sums.get(n).x2;
        double xy = sums.get(j+1).xy-sums.get(i).xy+r*sums.get(n).xy;
        double y2 = sums.get(j+1).y2-sums.get(i).y2+r*sums.get(n).y2;
        double k = j+1-i+r*n;

        ctr.x = x/k;
        ctr.y = y/k;

        double a = (x2-x*x/k)/k;
        double b = (xy-x*y/k)/k;
        double c = (y2-y*y/k)/k;

        double lambda2 = (a+c+Math.sqrt((a-c)*(a-c)+4*b*b))/2;

        a -= lambda2;
        c -= lambda2;

        if (Math.abs(a) >= Math.abs(c)) {
            l = Math.sqrt(a*a+b*b);
            if (l!=0) {
                dir.x = -b/l;
                dir.y = a/l;
            }
        } else {
            l = Math.sqrt(c*c+b*b);
            if (l!=0) {
                dir.x = -c/l;
                dir.y = b/l;
            }
        }
        if (l==0) {
            dir.x = dir.y = 0;
        }
    }

    public void adjustVertices() {
        int m = path.m;
        DoublePoint[] ctr = new DoublePoint[m];
        DoublePoint[] dir = new DoublePoint[m];
        double[] v = new double[3];
        Quad[] q = new Quad[m];
        DoublePoint s = new DoublePoint();
        List<Point> pt = path.pt;
        int x0 = path.x0;
        int y0 = path.y0;
        int n = path.len;
        int[] po = path.po;

        path.curve = new Curve(m);

        for (int i=0; i<m; i++) {
            int j = po[ProcessPath.mod(i+1,m)];
            j = ProcessPath.mod(j-po[i],n)+po[i];
            ctr[i] = new DoublePoint();
            dir[i] = new DoublePoint();
            pointslope(path, po[i], j, ctr[i], dir[i]);
        }

        for (int i=0; i<m; i++) {
            q[i] = new Quad();
            double d = dir[i].x * dir[i].x + dir[i].y * dir[i].y;
            if (d == 0.0) {
                for (int j=0; j<3; j++) {
                    for (int k=0; k<3; k++) {
                        q[i].data[j * 3 + k] = 0;
                    }
                }
            } else {
                v[0] = dir[i].y;
                v[1] = -dir[i].x;
                v[2] = - v[1] * ctr[i].y - v[0] * ctr[i].x;
                for (int l=0; l<3; l++) {
                    for (int k=0; k<3; k++) {
                        q[i].data[l * 3 + k] = v[l] * v[k] / d;
                    }
                }
            }
        }

        double dx, dy;
        double min, cand, xmin, ymin, z;
        for (int i=0; i<m; i++) {
            Quad Q = new Quad();
            DoublePoint w = new DoublePoint();

            s.x = pt.get(po[i]).getX()-x0;
            s.y = pt.get(po[i]).getY()-y0;

            int j = ProcessPath.mod(i-1,m);

            for (int l=0; l<3; l++) {
                for (int k=0; k<3; k++) {
                    Q.data[l * 3 + k] = q[j].at(l, k) + q[i].at(l, k);
                }
            }

            while(true) {

                double det = Q.at(0, 0)*Q.at(1, 1) - Q.at(0, 1)*Q.at(1, 0);
                if (det != 0.0) {
                    w.x = (-Q.at(0, 2)*Q.at(1, 1) + Q.at(1, 2)*Q.at(0, 1)) / det;
                    w.y = ( Q.at(0, 2)*Q.at(1, 0) - Q.at(1, 2)*Q.at(0, 0)) / det;
                    break;
                }

                if (Q.at(0, 0)>Q.at(1, 1)) {
                    v[0] = -Q.at(0, 1);
                    v[1] = Q.at(0, 0);
                } else if (Q.at(1, 1) != 0) {
                    v[0] = -Q.at(1, 1);
                    v[1] = Q.at(1, 0);
                } else {
                    v[0] = 1;
                    v[1] = 0;
                }
                double d = v[0] * v[0] + v[1] * v[1];
                v[2] = - v[1] * s.y - v[0] * s.x;
                for (int l=0; l<3; l++) {
                    for (int k=0; k<3; k++) {
                        Q.data[l * 3 + k] += v[l] * v[k] / d;
                    }
                }
            }
            dx = Math.abs(w.x-s.x);
            dy = Math.abs(w.y-s.y);
            if (dx <= 0.5 && dy <= 0.5) {
                path.curve.vertex[i] = new DoublePoint(w.x+x0, w.y+y0);
                continue;
            }

            min = ProcessPath.quadform(Q, s);
            xmin = s.x;
            ymin = s.y;

            if (Q.at(0, 0) != 0.0) {
                for (z=0; z<2; z++) {
                    w.y = s.y-0.5+z;
                    w.x = - (Q.at(0, 1) * w.y + Q.at(0, 2)) / Q.at(0, 0);
                    dx = Math.abs(w.x-s.x);
                    cand = ProcessPath.quadform(Q, w);
                    if (dx <= 0.5 && cand < min) {
                        min = cand;
                        xmin = w.x;
                        ymin = w.y;
                    }
                }
            }

            if (Q.at(1, 1) != 0.0) {
                for (z=0; z<2; z++) {
                    w.x = s.x-0.5+z;
                    w.y = - (Q.at(1, 0) * w.x + Q.at(1, 2)) / Q.at(1, 1);
                    dy = Math.abs(w.y-s.y);
                    cand = ProcessPath.quadform(Q, w);
                    if (dy <= 0.5 && cand < min) {
                        min = cand;
                        xmin = w.x;
                        ymin = w.y;
                    }
                }
            }

            for (int l=0; l<2; l++) {
                for (int k=0; k<2; k++) {
                    w.x = s.x-0.5+l;
                    w.y = s.y-0.5+k;
                    cand = ProcessPath.quadform(Q, w);
                    if (cand < min) {
                        min = cand;
                        xmin = w.x;
                        ymin = w.y;
                    }
                }
            }

            path.curve.vertex[i] = new DoublePoint(xmin + x0, ymin + y0);
        }
    }

}
