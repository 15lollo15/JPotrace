import java.awt.image.BufferedImage;
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

    public Point findNext(Point point) {
        int i = bm1.w * point.y + point.x;
        while (i < bm1.size && bm1.data[i] != 1) {
            i++;
        }
        if (i < bm1.size)
            return bm1.index(i);
        return null;
    }

    public int majority(int x, int y) {
        for (int i = 2; i < 5; i++) {
            int ct = 0;
            for (int a = -i + 1; a <= i - 1; a++) {
                ct += bm1.at(x + a, y + i - 1) ? 1 : -1;
                ct += bm1.at(x + i - 1, y + a - 1) ? 1 : -1;
                ct += bm1.at(x + a - 1, y - i) ? 1 : -1;
                ct += bm1.at(x - i, y + a) ? 1 : -1;
            }
            if (ct > 0) {
                return 1;
            } else if (ct < 0) {
                return 0;
            }
        }
        return 0;
    }

    public Path findPath(Point point) {
        Path path = new Path();
        path.sign = bm1.at(point.x, point.y) ? "+" : "-";

        int x = point.x;
        int y = point.y;

        int dirx = 0, diry = 1, tmp;

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

            if (x == point.x && y == point.y)
                break;

            boolean l = bm1.at(x + (dirx + diry - 1 ) / 2, y + (diry - dirx - 1) / 2);
            boolean r = bm1.at(x + (dirx - diry - 1) / 2, y + (diry + dirx - 1) / 2);

            if (r && !l) {
                if (info.turnpolicy == "right" ||
                        (info.turnpolicy == "black" && path.sign == "+") ||
                        (info.turnpolicy == "white" && path.sign == "-") ||
                        (info.turnpolicy == "majority" && majority(x, y) == 1) ||
                        (info.turnpolicy == "minority" && majority(x, y) == 0)) {
                    tmp = dirx;
                    dirx = -diry;
                    diry = tmp;
                } else {
                    tmp = dirx;
                    dirx = diry;
                    diry = -tmp;
                }
            } else if (r) {
                tmp = dirx;
                dirx = -diry;
                diry = tmp;
            } else if (!l) {
                tmp = dirx;
                dirx = diry;
                diry = -tmp;
            }
        }
        return path;
    }

    public void xorPath(Path path) {
        int y1 = path.pt.get(0).y;
        int len = path.len;
        for (int i = 1; i < len; i++) {
            int x = path.pt.get(i).x;
            int y = path.pt.get(i).y;

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
