package potrace;

import geometry.Path;
import geometry.IntegerPoint;
import utils.MathUtils;

import java.util.List;

public class BestPolygon {
    private Path path;

    public double penalty3(Path path, int i, int j) {
        int n = path.getLen();
        List<IntegerPoint> pt = path.getPoints();
        List<Sum> sums = path.getSums();
        double x;
        double y;
        double xy;
        double x2;
        double y2;
        double k;
        int r = 0;
        if (j>=n) {
            j -= n;
            r = 1;
        }

        if (r == 0) {
            x = sums.get(j+1).x - sums.get(i).x;
            y = sums.get(j+1).y - sums.get(i).y;
            x2 = sums.get(j+1).x2 - sums.get(i).x2;
            xy = sums.get(j+1).xy - sums.get(i).xy;
            y2 = sums.get(j+1).y2 - sums.get(i).y2;
            k = j+1.0 - i;
        } else {
            x = sums.get(j+1).x - sums.get(i).x + sums.get(n).x;
            y = sums.get(j+1).y - sums.get(i).y + sums.get(n).y;
            x2 = sums.get(j+1).x2 - sums.get(i).x2 + sums.get(n).x2;
            xy = sums.get(j+1).xy - sums.get(i).xy + sums.get(n).xy;
            y2 = sums.get(j+1).y2 - sums.get(i).y2 + sums.get(n).y2;
            k = j+1.0 - i + n;
        }

        double px = (pt.get(i).getX() + pt.get(j).getX()) / 2.0 - pt.get(0).getX();
        double py = (pt.get(i).getY() + pt.get(j).getY()) / 2.0 - pt.get(0).getY();
        double ey = (pt.get(j).getX() - pt.get(i).getX());
        double ex = -(pt.get(j).getY() - pt.get(i).getY());

        double a = ((x2 - 2*x*px) / k + px*px);
        double b = ((xy - x*py - y*px) / k + px*py);
        double c = ((y2 - 2*y*py) / k + py*py);

        double s = ex*ex*a + 2*ex*ey*b + ey*ey*c;

        return Math.sqrt(s);
    }

    public BestPolygon(Path path) {
        this.path = path;
    }

    public void bestPolygon() {
        int n = path.getLen();
        int[] clip0 = new int[n];
        int[] clip1 = new int[n + 1];
        int[] seg0 = new int[n + 1];
        int[] seg1 = new int[n + 1];
        double[] pen = new double[n + 1];

        int[] prev = new int[n + 1];

        for (int i=0; i<n; i++) {
            int c = MathUtils.mod(path.getLongestStraightLine()[MathUtils.mod(i-1,n)]-1,n);
            if (c == i) {
                c = MathUtils.mod(i+1,n);
            }
            if (c < i) {
                clip0[i] = n;
            } else {
                clip0[i] = c;
            }
        }

        int j = 1;
        for (int i=0; i<n; i++) {
            while (j <= clip0[i]) {
                clip1[j] = i;
                j++;
            }
        }

        int i = 0;
        for (j=0; i<n; j++) {
            seg0[j] = i;
            i = clip0[i];
        }
        seg0[j] = n;
        int m = j;

        i = n;
        for (j=m; j>0; j--) {
            seg1[j] = i;
            i = clip1[i];
        }
        seg1[0] = 0;

        pen[0]=0;
        for (j=1; j<=m; j++) {
            for (i=seg1[j]; i<=seg0[j]; i++) {
                double best = -1;
                for (int k=seg0[j-1]; k>=clip1[i]; k--) {
                    double thispen = penalty3(path, k, i) + pen[k];
                    if (best < 0 || thispen < best) {
                        prev[i] = k;
                        best = thispen;
                    }
                }
                pen[i] = best;
            }
        }

        int[] optimalPolygon = new int[m];
        for (i=n, j=m-1; i>0; j--) {
            i = prev[i];
            optimalPolygon[j] = i;
        }
        path.setOptimalPolygon(optimalPolygon);
    }
}
