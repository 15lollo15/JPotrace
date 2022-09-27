package potrace;

import geometry.Path;
import geometry.IntegerPoint;
import image.BooleanBitmap;

import java.util.ArrayList;
import java.util.List;

public class BooleanBitmapToPathList {
    private final BooleanBitmap bitmap;
    private final Settings settings;

    public BooleanBitmapToPathList(BooleanBitmap bitmap, Settings settings) {
        this.bitmap = bitmap.copy();
        this.settings = settings;
    }

    /**
     * Find the next black pixel/point
     * @param point Start point
     * @return The position of the first black pixel, or null if there's not
     */
    private IntegerPoint findNext(IntegerPoint point) {
        int i = bitmap.getWidth() * point.getY() + point.getX();
        while (i < bitmap.getSize() && !bitmap.at(i).booleanValue()) {
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
     * @param ray Ray to analyze
     * @return 0 if is balanced, > 0 if there is more black or < 0 if is more white
     */
    private BalanceStatus checkBalanceStatus(int x, int y, int ray) {
        int ct = 0;
        for (int a = -ray + 1; a <= ray - 1; a++) {
            ct += bitmap.at(x + a, y + ray - 1).booleanValue() ? 1 : -1;
            ct += bitmap.at(x + ray - 1, y + a - 1).booleanValue() ? 1 : -1;
            ct += bitmap.at(x + a - 1, y - ray).booleanValue() ? 1 : -1;
            ct += bitmap.at(x - ray, y + a).booleanValue() ? 1 : -1;
        }
        if (ct > 0)
            return BalanceStatus.MORE_BLACK;
        else if (ct < 0)
            return BalanceStatus.MORE_WHITE;
        return BalanceStatus.BALANCED;
    }

    private Path.Sign computeSign(IntegerPoint point) {
        return bitmap.at(point.getX(), point.getY()).booleanValue() ? Path.Sign.PLUS : Path.Sign.MINUS;
    }

    private boolean forceTurnRight(Path path, int x, int y) {
        return settings.getTurnPolicy().equals(TurnPolicy.RIGHT) ||
                (settings.getTurnPolicy().equals(TurnPolicy.BLACK) && path.getSign().equals(Path.Sign.PLUS)) ||
                (settings.getTurnPolicy().equals(TurnPolicy.WHITE) && path.getSign().equals(Path.Sign.MINUS)) ||
                (settings.getTurnPolicy().equals(TurnPolicy.MAJORITY) && majority(x, y)) ||
                (settings.getTurnPolicy().equals(TurnPolicy.MINORITY) && !majority(x, y));
    }

    private IntegerPoint chooseDirection(Path path, boolean r, boolean l, IntegerPoint oldDirection, IntegerPoint position) {
        int dirX = oldDirection.getX();
        int dirY = oldDirection.getY();
        int x = position.getX();
        int y = position.getY();
        if (r && !l) {
            if (forceTurnRight(path, x, y)) {
                int tmp = dirX;
                dirX = -dirY;
                dirY = tmp;
            } else {
                int tmp = dirX;
                dirX = dirY;
                dirY = -tmp;
            }
        } else if (r) {
            int tmp = dirX;
            dirX = -dirY;
            dirY = tmp;
        } else if (!l) {
            int tmp = dirX;
            dirX = dirY;
            dirY = -tmp;
        }
        return new IntegerPoint(dirX, dirY);
    }

    private Path findPath(IntegerPoint point) {
        Path path = new Path();
        path.setSign(computeSign(point));

        int x = point.getX();
        int y = point.getY();

        IntegerPoint direction = new IntegerPoint(0, 1);
        int maxX = x;
        int maxY = y;
        int minX = x;
        int minY = y;
        double area = path.getArea();
        while (true) {
            int dirX = direction.getX();
            int dirY = direction.getY();
            path.getPoints().add(new IntegerPoint(x, y));
            if (x > maxX)
                maxX = x;
            if (x < minX)
                minX = x;
            if (y > maxY)
                maxY = y;
            if (y < minY)
                minY = y;

            x += dirX;
            y += dirY;
            area -= x * dirY;

            if (x == point.getX() && y == point.getY())
                break;

            boolean l = bitmap.at(x + (dirX + dirY - 1 ) / 2, y + (dirY - dirX - 1) / 2);
            boolean r = bitmap.at(x + (dirX - dirY - 1) / 2, y + (dirY + dirX - 1) / 2);

            direction =  chooseDirection(path, r, l, direction, new IntegerPoint(x, y));
        }
        path.setArea(area);
        path.setMaxPoint(new IntegerPoint(maxX, maxY));
        path.setMinPoint(new IntegerPoint(minX, minY));
        return path;
    }

    private void xorPath(Path path) {
        int y1 = path.getPoints().get(0).getY();
        int len = path.getLen();
        for (int i = 1; i < len; i++) {
            int x = path.getPoints().get(i).getX();
            int y = path.getPoints().get(i).getY();

            if (y != y1) {
                int minY = Math.min(y1, y);
                int maxX = path.getMaxPoint().getX();
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

            if (path.getArea() > settings.getTurdSize()) {
                pathList.add(path);
            }
        }
        return pathList;
    }

    private enum BalanceStatus {MORE_BLACK, MORE_WHITE, BALANCED}
}
