package geometry;

import foo.Sum;

import java.util.ArrayList;
import java.util.List;

public class Path {
    public double area = 0;
    public int len = 0;
    public List<Point> pt = new ArrayList<>();
    public int minX = 100000;
    public int minY = 100000;
    public int maxX= -1;
    public int maxY = -1;
    public String sign;
    public int x0;
    public int y0;
    public List<Sum> sums;
    public int[] lon;
    public int m;
    public int[] po;
    public Curve curve;

    @Override
    public String toString() {
        return pt.toString();
    }
}
