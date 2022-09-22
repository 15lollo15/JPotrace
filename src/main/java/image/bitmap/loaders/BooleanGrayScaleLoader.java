package image.bitmap.loaders;

import image.BooleanBitmap;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BooleanGrayScaleLoader implements BitmapLoader<BooleanBitmap>{
    public static final int DEFAULT_THRESHOLD = 128;
    private int threshold;

    public BooleanGrayScaleLoader() {
        threshold = DEFAULT_THRESHOLD;
    }

    public BooleanGrayScaleLoader(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public BooleanBitmap load(BufferedImage img) {
        BooleanBitmap bm = new BooleanBitmap(img.getWidth(), img.getHeight());
        int[] data = bm.getData();

        int k = 0;
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                int rgb = img.getRGB(j, i);
                Color c = new Color(rgb);
                double color = 0.2126 * c.getRed() + 0.7153 * c.getGreen() +
                        0.0721 * c.getBlue();

                data[k++] = (color < threshold ? 1 : 0);
            }
        }
        return bm;
    }

    @Override
    public BooleanBitmap load(int w, int h, Color[] pixels) {
        BooleanBitmap bm = new BooleanBitmap(w, h);
        int[] data = bm.getData();

        for (int i = 0; i < data.length; i++) {
            Color c = pixels[i];
            double color = 0.2126 * c.getRed() + 0.7153 * c.getGreen() +
                    0.0721 * c.getBlue();
            data[i] = (color < threshold ? 1 : 0);
        }


        return bm;
    }
}
