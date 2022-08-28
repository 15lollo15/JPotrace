package foo;

import geometry.Curve;
import geometry.Path;
import image.Bitmap;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class GetSVG {
    public static final String RGB_STRING_FORMAT = "rgb(%d,%d,%d)";

    private final int size;
    private final String optType;
    private final Bitmap bm;
    private final List<Path> pathlist;

    public GetSVG(int size, String optType, Bitmap bm, List<Path> pathList) {
        this.size = size;
        this.optType = optType;
        this.bm = bm;
        this.pathlist = pathList;
    }

    private String format(double d) {
        return BigDecimal.valueOf(d).setScale(3, RoundingMode.HALF_UP).toString();
    }

    public String  bezier(Curve curve, int i) {
        var b = "C " + format(curve.c[i * 3].x * size) + " " +
                format(curve.c[i * 3].y * size) + ",";
        b += format(curve.c[i * 3 + 1].x * size) + ' ' +
                format(curve.c[i * 3 + 1].y * size)+ ',';
        b += format(curve.c[i * 3 + 2].x * size)+ ' ' +
                format(curve.c[i * 3 + 2].y * size) + ' ';
        return b;
    }

    public String  segment(Curve curve, int i) {
        var s = "L " + format(curve.c[i * 3 + 1].x * size) + ' ' +
                format(curve.c[i * 3 + 1].y * size) + ' ';
        s += format(curve.c[i * 3 + 2].x * size) + ' ' +
                format(curve.c[i * 3 + 2].y * size) + ' ';
        return s;
    }

    public String path(Curve curve) {
        StringBuilder sb = new StringBuilder();
        int n = curve.n;
        sb.append("M").append(format(curve.c[(n - 1) * 3 + 2].x * size));
        sb.append(" ").append(format(curve.c[(n - 1) * 3 + 2].y * size)).append(" ");
        for (int i = 0; i < n; i++) {
            if (curve.tag[i].equals("CURVE")) {
                sb.append(bezier(curve, i));
            } else if (curve.tag[i].equals("CORNER")) {
                sb.append(segment(curve, i));
            }
        }
        return sb.toString();
    }

    public String getSVG() {
        return getSVG(Color.BLACK);
    }

    public String getSVG(Color color) {
        int w = bm.getWidth() * size;
        int h = bm.getHeight() * size;
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
            Curve c = path.curve;
            sb.append(path(c));
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

    public String getPath(Color color) {
        StringBuilder sb = new StringBuilder();
        sb.append("<path d=\"");
        String strokec;
        String fillc;
        String fillrule;
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        for (Path path : pathlist) {
            Curve c = path.curve;
            sb.append(path(c));
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
