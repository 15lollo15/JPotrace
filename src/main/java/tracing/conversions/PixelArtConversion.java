package tracing.conversions;

import image.ColorBitmap;
import image.bitmap.loaders.ColorBitmapLoader;
import image.palette.AllColorsExtractor;
import tracing.base.GetSVG;
import utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class PixelArtConversion implements ColorConversion{
    public static final int SCALE_FACTOR = 10;
    public static final int MIN_SIZE = 128;
    private Consumer<String> statusCallback;

    @Override
    public String convert(BufferedImage img, int scale) {
        if (img.getHeight() < MIN_SIZE && img.getWidth() < MIN_SIZE) {
            log("Upscaling");
            img = ImageUtils.upscale(img, SCALE_FACTOR);
        }
        ColorBitmap bitmap = new ColorBitmapLoader().load(img);
        Color[] pixels = bitmap.getData();

        log("Palette extraction");
        Set<Color> palette = new AllColorsExtractor().extract(pixels);

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
