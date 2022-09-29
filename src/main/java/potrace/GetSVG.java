package potrace;

import geometry.Curve;
import geometry.DoublePoint;
import geometry.Path;
import geometry.Tag;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class GetSVG {
    public static final String RGB_STRING_FORMAT = "rgb(%d,%d,%d)";

    private GetSVG() {}

    private static String format(double d) {
        return BigDecimal.valueOf(d).setScale(3, RoundingMode.HALF_UP).toString();
    }

    private static String  bezier(Curve curve, int i, int size) {
        DoublePoint[] c = curve.getControlPoints();
        var b = "C " + format(c[i * 3].getX() * size) + " " +
                format(c[i * 3].getY() * size) + ",";
        b += format(c[i * 3 + 1].getX() * size) + ' ' +
                format(c[i * 3 + 1].getY() * size)+ ',';
        b += format(c[i * 3 + 2].getX() * size)+ ' ' +
                format(c[i * 3 + 2].getY() * size) + ' ';
        return b;
    }

    private static String  segment(Curve curve, int i, int size) {
        DoublePoint[] c = curve.getControlPoints();
        var s = "L " + format(c[i * 3 + 1].getX() * size) + ' ' +
                format(c[i * 3 + 1].getY() * size) + ' ';
        s += format(c[i * 3 + 2].getX() * size) + ' ' +
                format(c[i * 3 + 2].getY() * size) + ' ';
        return s;
    }

    private static String path(Curve curve, int size) {
        StringBuilder sb = new StringBuilder();
        int n = curve.getVertex().length;
        DoublePoint[] c = curve.getControlPoints();
        sb.append("M").append(format(c[(n - 1) * 3 + 2].getX() * size));
        sb.append(" ").append(format(c[(n - 1) * 3 + 2].getY() * size)).append(" ");
        for (int i = 0; i < n; i++) {
            if (curve.getTag()[i] == Tag.CURVE) {
                sb.append(bezier(curve, i, size));
            } else if (curve.getTag()[i] == Tag.CORNER) {
                sb.append(segment(curve, i, size));
            }
        }
        return sb.toString();
    }

    public static String getSVG(int width, int height, int size, List<Path> pathlist,  String optType) {
        return getSVG(width, height, size, pathlist, optType, Color.BLACK);
    }

    private static String getSVG(int width, int height, int size, List<Path> pathlist, String optType, Color color) {
        int w = width * size;
        int h = height * size;
        String strokec;
        String fillc;
        String fillrule;
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        StringBuilder sb = new StringBuilder();
        sb.append("<svg id=\"svg\" version=\"1.1\" width=\"").append(w).append("\" height=\"").append(h);
        sb.append("\" xmlns=\"http://www.w3.org/2000/svg\">");
        sb.append("<path d=\"");
        for (Path path : pathlist) {
            Curve c = path.getCurve();
            sb.append(path(c, size));
        }
        if (optType.equals("curve")) {
            strokec = String.format(RGB_STRING_FORMAT, r, g, b);
            fillc = "none";
            fillrule = "";
        } else {
            strokec = "none";
            fillc = String.format(RGB_STRING_FORMAT, r, g, b);
            fillrule = " fill-rule=\"evenodd\"";
        }
        sb.append("\" stroke=\"").append(strokec);
        sb.append("\" fill=\"").append(fillc).append("\"").append(fillrule).append("/></svg>");
        return sb.toString();
    }

    public static String getSVG(int width, int height, int size, String optType, Color[] colors, List<Path>[] paths) {
        int w = width * size;
        int h = height * size;

        String svg = "<svg id=\"svg\" version=\"1.1\" width=\"" + w + "\" height=\"" + h +
                "\" xmlns=\"http://www.w3.org/2000/svg\">";


        for (int i = paths.length - 1; i >= 0; i--) {
            Color color = colors[i];
            List<Path> pathlist = paths[i];

            svg += getPath(pathlist, size, optType, color);
        }

        svg += "</svg>";
        return svg;
    }

    private static String getPath(List<Path> pathlist, int size, String optType, Color color) {
        StringBuilder sb = new StringBuilder();
        sb.append("<path d=\"");
        String strokec;
        String fillc;
        String fillrule;
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        for (Path path : pathlist) {
            Curve c = path.getCurve();
            sb.append(path(c, size));
        }
        if (optType.equals("curve")) {
            strokec = String.format(RGB_STRING_FORMAT, r, g, b);
            fillc = "none";
            fillrule = "";
        } else {
            strokec = "none";
            fillc = String.format(RGB_STRING_FORMAT, r, g, b);
            fillrule = " fill-rule=\"evenodd\"";
        }
        sb.append("\" stroke=\"").append(strokec);
        sb.append("\" fill=\"").append(fillc).append("\"").append(fillrule).append("/>");

        return sb.toString();
    }
}
