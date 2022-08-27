package image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GrayScaleLoader implements BitmapLoader{
    public static final int DEFAULT_THRESHOLD = 128;
    private int threshold;

    public GrayScaleLoader() {
        threshold = DEFAULT_THRESHOLD;
    }

    public GrayScaleLoader(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public Bitmap load(BufferedImage img) {
        Bitmap bm = new Bitmap(img.getWidth(), img.getHeight());
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
    public Bitmap load(int w, int h, Color[] pixels) {
        Bitmap bm = new Bitmap(w, h);
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
