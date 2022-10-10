package geometry;

import tracing.base.Sum;

import java.util.ArrayList;
import java.util.List;

public class Path {
    private double area = 0;
    private List<IntegerPoint> points = new ArrayList<>();
    private IntegerPoint minPoint;
    private IntegerPoint maxPoint;
    private Sign sign;
    private List<Sum> sums = new ArrayList<>();
    private int[] longestStraightLine;
    private int[] optimalPolygon;
    private Curve curve;

    @Override
    public String toString() {
        return points.toString();
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public int getLen() {
        return points.size();
    }

    public IntegerPoint getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(IntegerPoint minPoint) {
        this.minPoint = minPoint;
    }

    public IntegerPoint getMaxPoint() {
        return maxPoint;
    }

    public void setMaxPoint(IntegerPoint maxPoint) {
        this.maxPoint = maxPoint;
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public List<IntegerPoint> getPoints() {
        return points;
    }

    public IntegerPoint getFirstPoint() {
        return points.get(0);
    }

    public List<Sum> getSums() {
        return sums;
    }

    public int[] getLongestStraightLine() {
        return longestStraightLine;
    }

    public void setLongestStraightLine(int[] longestStraightLine) {
        this.longestStraightLine = longestStraightLine;
    }

    public int[] getOptimalPolygon() {
        return optimalPolygon;
    }

    public void setOptimalPolygon(int[] optimalPolygon) {
        this.optimalPolygon = optimalPolygon;
    }

    public Curve getCurve() {
        return curve;
    }

    public void setCurve(Curve curve) {
        this.curve = curve;
    }

    public enum Sign {PLUS, MINUS}
}
