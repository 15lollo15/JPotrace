package potrace;

import geometry.Path;
import geometry.IntegerPoint;
import image.BooleanBitmap;

import java.util.ArrayList;
import java.util.List;

public class BooleanBitmapToPathList {
    private BooleanBitmap bitmap;
    private Info info;

    public BooleanBitmapToPathList(BooleanBitmap bitmap, Info info) {
        this.bitmap = bitmap.copy();
        this.info = info;
    }

    /**
     * Find the next black pixel/point
     * @param point Start point
     * @return The position of the first black pixel, or null if there's not
     */
    private IntegerPoint findNext(IntegerPoint point) {
        int i = bitmap.getWidth() * point.getY() + point.getX();
        while (i < bitmap.getSize() && !bitmap.at(i)) {
            i++;
        }
        if (i < bitmap.getSize())
            return bitmap.index(i);
        return null;
    }

    /**
     * Verify there is a majority of black pixels near point(x,y)
     * @param x X of the point
     * @param y Y of the point
     * @return True if there is a majority of black pixels, false otherwise
     */
    private boolean majority(int x, int y) {
        for (int ray = 2; ray < 5; ray++) {
            BalanceStatus bs = checkBalanceStatus(x, y, ray);
            if (bs.equals(BalanceStatus.MORE_BLACK)) {
                return true;
            } else if (bs.equals(BalanceStatus.MORE_WHITE)) {
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
    private BalanceStatus checkBalanceStatus(int x, int y, int ray) {
        int ct = 0;
        for (int a = -ray + 1; a <= ray - 1; a++) {
            ct += bitmap.at(x + a, y + ray - 1) ? 1 : -1;
            ct += bitmap.at(x + ray - 1, y + a - 1) ? 1 : -1;
            ct += bitmap.at(x + a - 1, y - ray) ? 1 : -1;
            ct += bitmap.at(x - ray, y + a) ? 1 : -1;
        }
        if (ct > 0)
            return BalanceStatus.MORE_BLACK;
        else if (ct < 0)
            return BalanceStatus.MORE_WHITE;
        return BalanceStatus.BALANCED;
    }

    private Path findPath(IntegerPoint integerPoint) {
        Path path = new Path();
        path.sign = bitmap.at(integerPoint.getX(), integerPoint.getY()) ? Path.Sign.PLUS : Path.Sign.MINUS;

        int x = integerPoint.getX();
        int y = integerPoint.getY();

        int dirx = 0;
        int diry = 1;
        int maxX = x;
        int maxY = y;
        int minX = x;
        int minY = y;
        while (true) {
            path.pt.add(new IntegerPoint(x, y));
            if (x > maxX)
                maxX = x;
            if (x < minX)
                minX = x;
            if (y > maxY)
                maxY = y;
            if (y < minY)
                minY = y;
            path.len++;

            x += dirx;
            y += diry;
            path.area -= x * diry;

            if (x == integerPoint.getX() && y == integerPoint.getY())
                break;

            boolean l = bitmap.at(x + (dirx + diry - 1 ) / 2, y + (diry - dirx - 1) / 2);
            boolean r = bitmap.at(x + (dirx - diry - 1) / 2, y + (diry + dirx - 1) / 2);

            if (r && !l) {
                if (info.turnpolicy.equals(TurnPolicy.RIGHT) ||
                        (info.turnpolicy.equals(TurnPolicy.BLACK) && path.sign.equals(Path.Sign.PLUS)) ||
                        (info.turnpolicy.equals(TurnPolicy.WHITE) && path.sign.equals(Path.Sign.MINUS)) ||
                        (info.turnpolicy.equals(TurnPolicy.MAJORITY) && majority(x, y)) ||
                        (info.turnpolicy.equals(TurnPolicy.MINORITY) && !majority(x, y))) {
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
        path.maxPoint = new IntegerPoint(maxX, maxY);
        path.minPoint = new IntegerPoint(minX, minY);
        return path;
    }

    private void xorPath(Path path) {
        int y1 = path.pt.get(0).getY();
        int len = path.len;
        for (int i = 1; i < len; i++) {
            int x = path.pt.get(i).getX();
            int y = path.pt.get(i).getY();

            if (y != y1) {
                int minY = y1 < y ? y1 : y;
                int maxX = path.maxPoint.getX();
                for (int j = x; j < maxX; j++) {
                    bitmap.flip(j, minY);
                }
                y1 = y;
            }
        }
    }

    public List<Path> toPathList() {
        List<Path> pathList = new ArrayList<>();
        IntegerPoint currentPoint = new IntegerPoint(0, 0);
        while ((currentPoint = findNext(currentPoint)) != null) {

            Path path = findPath(currentPoint);

            xorPath(path);

            if (path.area > info.turdsize) {
                pathList.add(path);
            }
        }
        return pathList;
    }

    private enum BalanceStatus {MORE_BLACK, MORE_WHITE, BALANCED}
}
