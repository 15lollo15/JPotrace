package utils;

import com.github.brankale.jcolorspace.colorspace.ColorSpaceUtils;
import com.github.brankale.jcolorspace.colorspaces.ColorSpaces;
import com.github.brankale.jcolorspace.utils.FloatArray;

import java.awt.*;

public class ColorsUtils {
    private ColorsUtils() {}

    public static double distance(Color c1, Color c2) {
        FloatArray color1 = new FloatArray(c1.getRed() / 255f, c1.getGreen() / 255f, c1.getBlue() / 255f);
        FloatArray color2 = new FloatArray(c2.getRed() / 255f, c2.getGreen() / 255f, c2.getBlue() / 255f);
        return ColorSpaceUtils.deltaE(ColorSpaces.SRGB, color1, color2);
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
