package tracing.conversions;

import image.ColorBitmap;
import image.Filters;
import image.bitmap.loaders.ColorBitmapLoader;
import image.palette.ElbowKMeansExtractor;
import image.palette.KMeansExtractor;
import tracing.base.GetSVG;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class KMeansColorConversion implements ColorConversion{
    public static final int AUTO_CHOOSE_NUMBER_OF_COLORS = -1;
    public static final double PALETTE_CLEAN_THRESHOLD = 15;

    private final int numColors;
    private final int blur;
    private Consumer<String> statusCallback;

    public KMeansColorConversion(int numColors, int blur) {
        this.numColors = numColors;
        this.blur = blur;
    }

    public KMeansColorConversion() {
        this(AUTO_CHOOSE_NUMBER_OF_COLORS, 1);
    }

    @Override
    public String convert(BufferedImage img, int scale) {
        ColorBitmap bitmap = new ColorBitmapLoader().load(img);
        log("Blurring");
        bitmap = Filters.blur(bitmap, blur);
        Color[] pixels = bitmap.getData();

        log("Palette extraction");
        Set<Color> palette = null;
        if (numColors == AUTO_CHOOSE_NUMBER_OF_COLORS) {
            palette = new ElbowKMeansExtractor().extract(pixels);
        } else {
            palette = new KMeansExtractor(numColors).extract(pixels);
        }
        log("Palette simplification");
        ColorConversion.simplify(bitmap, palette);
        ColorConversion.cleanPalette(palette, bitmap.getData(), PALETTE_CLEAN_THRESHOLD);
        log("Image simplification");
        ColorConversion.simplify(bitmap, palette);

        log("Paths extraction and curve generation");
        List<ColorPaths> colorPaths = ColorConversion.extractFigures(bitmap, palette);

        log("SVG generation");
        return GetSVG.getSVG(img.getWidth(), img.getHeight(), scale, "", colorPaths);
    }

    @Override
    public String convert(BufferedImage img) {
        return convert(img, 1);
    }

    private void log(String msg) {
        if (statusCallback != null)
            statusCallback.accept(msg);
    }

    @Override
    public void setStatusCallback(Consumer<String> callback) {
        this.statusCallback = callback;
    }
}
