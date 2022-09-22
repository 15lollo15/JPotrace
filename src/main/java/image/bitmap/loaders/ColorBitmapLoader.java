package image.bitmap.loaders;

import image.ColorBitmap;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorBitmapLoader implements BitmapLoader<ColorBitmap> {
    @Override
    public ColorBitmap load(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        ColorBitmap bitmap = new ColorBitmap(width, height);
        for (int x = 0; x < img.getWidth(); x++)
            for (int y = 0; y < img.getHeight(); y++)
                bitmap.set(x, y, new Color(img.getRGB(x, y)));
        return bitmap;
    }

    @Override
    public ColorBitmap load(int w, int h, Color[] pixels) {
        ColorBitmap bitmap = new ColorBitmap(w, h);
        for (int i = 0; i < pixels.length; i++)
            bitmap.set(i, pixels[i]);
        return bitmap;
    }
}
