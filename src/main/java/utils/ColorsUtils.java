package utils;

import com.github.brankale.jcolorspace.colorspace.ColorSpaceUtils;
import com.github.brankale.jcolorspace.colorspace.connector.Connector;
import com.github.brankale.jcolorspace.colorspaces.ColorSpaces;
import com.github.brankale.jcolorspace.utils.FloatArray;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorsUtils {
    private static final Map<Color, FloatArray> cache = new HashMap<>();
    private ColorsUtils() {}

    public static double distance(Color c1, Color c2) {
        cache.computeIfAbsent(c1, ColorsUtils::toCieLab);
        cache.computeIfAbsent(c2, ColorsUtils::toCieLab);

        FloatArray color1 = cache.get(c1);
        FloatArray color2 = cache.get(c2);
        return ColorSpaceUtils.deltaE(ColorSpaces.CIE_LAB, color1, color2);
    }

    public static FloatArray toCieLab(Color c) {
        Connector connector = ColorSpaces.SRGB.connect(ColorSpaces.CIE_LAB);
        double[] norm = normalize(c);
        return connector.transform((float) norm[0], (float) norm[1], (float) norm[2]);
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
