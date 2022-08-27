package foo;

import geometry.Path;
import geometry.Point;
import image.Bitmap;

import java.util.List;

public class BmToPathlist {
    private Bitmap bm1;
    private Info info;
    private List<Path> pathlist;

    public BmToPathlist(Bitmap bm, Info info, List<Path> pathList) {
        bm1 = bm.copy();
        this.info = info;
        this.pathlist = pathList;
    }

    /**
     * Find the next black pixel/point
     * @param point Start point
     * @return The position of the first black pixel, or null if there's not
     */
    public Point findNext(Point point) {
        int i = bm1.getWidth() * point.getY() + point.getX();
        int[] data = bm1.getData();
        while (i < data.length && data[i] != 1) {
            i++;
        }
        if (i < bm1.getSize())
            return bm1.index(i);
        return null;
    }

    /**
     * Verify there is a majority of black pixels near point(x,y)
     * @param x X of the point
     * @param y Y of the point
     * @return True if there is a majority of black pixels, false otherwise
     */
    public boolean majority(int x, int y) {
        for (int ray = 2; ray < 5; ray++) {
            int ct = isBalanced(x, y, ray);
            if (ct > 0) {
                return true;
            } else if (ct < 0) {
                return false;
            }
        }
        return false;
    }

    /**
     * Verify if image is balanced near point(x,y)
     * @param x X of the point
     * @param y Y of the point
     * @param ray Ray to analize
     * @return 0 if is balanced, > 0 if there is more black or < 0 if is more white
     */
    private int isBalanced(int x, int y, int ray) {
        int ct = 0;
        for (int a = -ray + 1; a <= ray - 1; a++) {
            ct += bm1.at(x + a, y + ray - 1) ? 1 : -1;
            ct += bm1.at(x + ray - 1, y + a - 1) ? 1 : -1;
            ct += bm1.at(x + a - 1, y - ray) ? 1 : -1;
            ct += bm1.at(x - ray, y + a) ? 1 : -1;
        }
        return ct;
    }

    public Path findPath(Point point) {
        Path path = new Path();
        path.sign = bm1.at(point.getX(), point.getY()) ? "+" : "-";

        int x = point.getX();
        int y = point.getY();

        int dirx = 0;
        int diry = 1;

        while (true) {
            path.pt.add(new Point(x, y));
            if (x > path.maxX)
                path.maxX = x;
            if (x < path.minX)
                path.minX = x;
            if (y > path.maxY)
                path.maxY = y;
            if (y < path.minY)
                path.minY = y;
            path.len++;

            x += dirx;
            y += diry;
            path.area -= x * diry;

            if (x == point.getX() && y == point.getY())
                break;

            boolean l = bm1.at(x + (dirx + diry - 1 ) / 2, y + (diry - dirx - 1) / 2);
            boolean r = bm1.at(x + (dirx - diry - 1) / 2, y + (diry + dirx - 1) / 2);

            if (r && !l) {
                if (info.turnpolicy.equals("right") ||
                        (info.turnpolicy.equals("black") && path.sign.equals("+")) ||
                        (info.turnpolicy.equals("white") && path.sign.equals("-")) ||
                        (info.turnpolicy.equals("majority") && majority(x, y)) ||
                        (info.turnpolicy.equals("minority") && !majority(x, y))) {
                    int tmp = dirx;
                    dirx = -diry;
                    diry = tmp;
                } else {
                    int tmp = dirx;
                    dirx = diry;
                    diry = -tmp;
                }
            } else if (r) {
                int tmp = dirx;
                dirx = -diry;
                diry = tmp;
            } else if (!l) {
                int tmp = dirx;
                dirx = diry;
                diry = -tmp;
            }
        }
        return path;
    }

    public void xorPath(Path path) {
        int y1 = path.pt.get(0).getY();
        int len = path.len;
        for (int i = 1; i < len; i++) {
            int x = path.pt.get(i).getX();
            int y = path.pt.get(i).getY();

            if (y != y1) {
                int minY = y1 < y ? y1 : y;
                int maxX = path.maxX;
                for (int j = x; j < maxX; j++) {
                    bm1.flip(j, minY);
                }
                y1 = y;
            }
        }
    }

    public void bmToPathlist() {
        Point currentPoint = new Point(0, 0);
        while ((currentPoint = findNext(currentPoint)) != null) {

            Path path = findPath(currentPoint);

            xorPath(path);

            if (path.area > info.turdsize) {
                pathlist.add(path);
            }
        }
    }

}
