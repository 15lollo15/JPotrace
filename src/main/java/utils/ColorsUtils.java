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

    public static double[] normalize(Color c) {
        double[] normalized = new double[3];
        normalized[0] = c.getRed()  / 255d;
        normalized[1] = c.getGreen()  / 255d;
        normalized[2] = c.getBlue()  / 255d;
        return normalized;
    }

    public static double[] normalizedLuminance(Color c) {
        double[] normalized = normalize(c);
        for (int i = 0; i < normalized.length; i++)
            normalized[i] = Math.pow(normalized[i], 2.2);
        return  normalized;
    }

    public static double normalizedGrayScale(Color c) {
        double[] normalizedLuminance = normalizedLuminance(c);
        return normalizedLuminance[0] * 0.2126 + normalizedLuminance[1] * 0.7153 + normalizedLuminance[2] * 0.0721;
    }

}
