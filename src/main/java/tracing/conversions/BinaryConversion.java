package tracing.conversions;

import geometry.Path;
import image.BooleanBitmap;
import image.bitmap.loaders.BooleanGrayScaleLoader;
import tracing.base.BooleanBitmapToPathList;
import tracing.base.GetSVG;
import tracing.base.ProcessPath;
import tracing.base.Settings;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class BinaryConversion implements Conversion{
    public static final int DEFAULT_THRESHOLD = 128;

    private final int threshold;
    private final Settings settings;
    private Consumer<String> statusCallback;

    public BinaryConversion(int threshold, Settings settings) {
        this.threshold = threshold;
        this.settings = settings;
    }

    public BinaryConversion(int threshold) {
        this(threshold, new Settings());
    }

    public BinaryConversion() {
        this(DEFAULT_THRESHOLD, new Settings());
    }

    @Override
    public String convert(BufferedImage img, int scale) {
        log("Grayscale and binarize image");
        BooleanGrayScaleLoader loader = new BooleanGrayScaleLoader(threshold);
        BooleanBitmap bm = loader.load(img);
        log("Paths extraction");
        BooleanBitmapToPathList booleanBitmapToPathlist = new BooleanBitmapToPathList(bm, settings);
        List<Path> pathList = booleanBitmapToPathlist.toPathList();
        log("Curve generation");
        ProcessPath processPath = new ProcessPath(settings, pathList);
        processPath.processPath();
        log("SVG generation");
        return  GetSVG.getSVG(img.getWidth(), img.getHeight(), scale, pathList, "");
    }

    private void log(String msg) {
        if (statusCallback != null)
            statusCallback.accept(msg);
    }

    @Override
    public String convert(BufferedImage img) {
        return convert(img, 1);
    }

    @Override
    public void setStatusCallback(Consumer<String> callback) {
        this.statusCallback = callback;
    }
}
