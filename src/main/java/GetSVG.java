import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class GetSVG {
    private int size;
    private String opt_type;
    private Bitmap bm;
    private List<Path> pathlist;

    public GetSVG(int size, String opt_type, Bitmap bm, List<Path> pathList) {
        this.size = size;
        this.opt_type = opt_type;
        this.bm = bm;
        this.pathlist = pathList;
    }

    private String format(double d) {
        return String.format("%.3f", d).replace(',', '.');
    }

    public String  bezier(Curve curve, int i) {
        var b = "C " + format(curve.c[i * 3 + 0].x * size) + " " +
                format(curve.c[i * 3 + 0].y * size) + ",";
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
        int n = curve.n;
        var p = 'M' + format(curve.c[(n - 1) * 3 + 2].x * size) +
                ' ' + format(curve.c[(n - 1) * 3 + 2].y * size) + ' ';
        for (int i = 0; i < n; i++) {
            if (curve.tag[i].equals("CURVE")) {
                p += bezier(curve, i);
            } else if (curve.tag[i].equals("CORNER")) {
                p += segment(curve, i);
            }
        }
        return p;
    }

    public String getSVG() {
        int w = bm.getWidth() * size;
        int h = bm.getHeight() * size;
        int len = pathlist.size();
        String strokec, fillc, fillrule;

        String svg = "<svg id=\"svg\" version=\"1.1\" width=\"" + w + "\" height=\"" + h +
                "\" xmlns=\"http://www.w3.org/2000/svg\">";
        svg += "<path d=\"";
        for (int i = 0; i < len; i++) {
            Curve c = pathlist.get(i).curve;
            svg += path(c);
        }
        if (opt_type.equals("curve")) {
            strokec = "black";
            fillc = "none";
            fillrule = "";
        } else {
            strokec = "none";
            fillc = "black";
            fillrule = " fill-rule=\"evenodd\"";
        }
        svg += "\" stroke=\"" + strokec + "\" fill=\"" + fillc + "\"" + fillrule + "/></svg>";
        return svg;
    }
}
