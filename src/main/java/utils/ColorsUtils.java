package utils;

import java.awt.*;

public class ColorsUtils {
    private ColorsUtils() {}

    public static double distance(Color c1, Color c2) {
        double rQuad = MathUtils.square((double) c1.getRed() - c2.getRed());
        double gQuad = MathUtils.square((double) c1.getGreen() - c2.getGreen());
        double bQuad = MathUtils.square((double) c1.getBlue() - c2.getBlue());
        return Math.sqrt(rQuad + gQuad + bQuad);
    }

}
