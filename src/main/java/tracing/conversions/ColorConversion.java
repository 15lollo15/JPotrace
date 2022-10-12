package tracing.conversions;

import geometry.Path;
import image.BooleanBitmap;
import image.ColorBitmap;
import image.bitmap.loaders.BooleanColorPickerLoader;
import tracing.base.BooleanBitmapToPathList;
import tracing.base.ProcessPath;
import tracing.base.Settings;
import utils.ColorsUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

public interface ColorConversion extends Conversion{
    static List<ColorPaths> extractFigures(ColorBitmap img, Set<Color> palette, Settings settings) {
        List<ColorPaths> colorPaths = new ArrayList<>();
        List<Color> colorsToPick = new ArrayList<>();
        for (Color c : palette) {
            colorsToPick.add(c);
            BooleanColorPickerLoader loader = new BooleanColorPickerLoader(colorsToPick);
            BooleanBitmap bm = loader.load(img.getWidth(), img.getHeight(), img.getData());

            BooleanBitmapToPathList booleanBitmapToPathlist = new BooleanBitmapToPathList(bm, settings);
            List<Path> pathList = booleanBitmapToPathlist.toPathList();

            ProcessPath processPath = new ProcessPath(settings, pathList);
            processPath.processPath();

            colorPaths.add(new ColorPaths(c, pathList));
        }
        return colorPaths;
    }

    static List<ColorPaths> extractFigures(ColorBitmap img, Set<Color> palette) {
        return extractFigures(img, palette, new Settings());
    }

    static void simplify(ColorBitmap img, Set<Color> palette) {
        Map<Color, Color> cache = new HashMap<>();
        Color[] colors = img.getData();
        for (int i = 0; i < colors.length; i++) {
            Color color = colors[i];
            Color similar;
            if (!cache.containsKey(color)) {
                similar = findSimilar(color, palette);
                cache.put(color, similar);
            }else {
                similar = cache.get(color);
            }
            img.set(i,similar);
        }
    }

    static Color findSimilar(Color color, Set<Color> palette) {
        Color similar = null;
        for (Color paletteColor : palette) {
            if (similar == null) {
                similar = paletteColor;
                continue;
            }
            if (ColorsUtils.distance(color, paletteColor) < ColorsUtils.distance(color, similar))
                similar = paletteColor;
        }
        return similar;
    }

    static void cleanPalette(Set<Color> palette, Color[] pixels, double threshold) {
        Map<Color, Integer> pixelsForColor =  countPixelsForColor(pixels);
        List<Color> colors = palette.stream().toList();
        for (int i = 0; i < colors.size(); i++) {
            Color c1 = colors.get(i);
            for (int j = i + 1; j < colors.size(); j++) {
                Color c2 = colors.get(j);
                if (palette.contains(c2)) {
                    double diff = ColorsUtils.distance(c1, c2);

                    if (diff >= threshold)
                        continue;

                    int numPixels1 = pixelsForColor.get(c1);
                    int numPixels2 = pixelsForColor.get(c2);

                    if (numPixels1 > numPixels2)
                        palette.remove(c2);
                    else
                        palette.remove(c1);
                }
            }
        }
    }

    static Map<Color, Integer> countPixelsForColor(Color[] pixels) {
        Map<Color, Integer> pixelsForColor = new HashMap<>();

        for (Color pixel : pixels) {
            pixelsForColor.putIfAbsent(pixel, 0);
            int oldValue = pixelsForColor.get(pixel);
            pixelsForColor.put(pixel, oldValue + 1);
        }

        return pixelsForColor;
    }
}
