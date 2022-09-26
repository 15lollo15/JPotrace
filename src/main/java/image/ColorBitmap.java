package image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorBitmap implements Bitmap<Color>{
    private final int width;
    private final int height;
    private final int size;
    private final Color[] data;

    public ColorBitmap(int width, int height) {
        this.width = width;
        this.height = height;
        size = width * height;
        data = new Color[width * height];
    }

    @Override
    public Color at(int x, int y) {
        return data[toIndex(x, y)];
    }

    @Override
    public Color at(int i) {
        return data[i];
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getSize() {
        return size;
    }

    public Color[] getData() {
        return data;
    }

    @Override
    public void set(int x, int y, Color value) {
        data[toIndex(x, y)] = value;
    }

    @Override
    public void set(int i, Color value) {
        data[i] = value;
    }

    @Override
    public BufferedImage toBufferedImage() {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                img.setRGB(x, y, at(x, y).getRGB());
        return img;
    }

    public ColorBitmap copy() {
        ColorBitmap bm = new ColorBitmap(width, height);
        if (size >= 0)
            System.arraycopy(this.data, 0, bm.data, 0, this.size);
        return bm;
    }

    private int toIndex(int x, int y) {
        return width * y + x;
    }

    public static ColorBitmap overlay(ColorBitmap cb1, ColorBitmap cb2) {
        ColorBitmap overlay = new ColorBitmap(cb1.getWidth(), cb1.getHeight());

        for (int x = 0; x < cb1.getWidth(); x++) {
            for (int y = 0; y < cb1.getHeight(); y++) {
                overlay.set(x, y, overlay(cb1.at(x, y), cb2.at(x, y)));
            }
        }

        return overlay;
    }

    public static Color overlay(Color color1, Color color2) {
        int r = Math.min(color1.getRed() + color2.getRed(), 255);
        int g = Math.min(color1.getGreen() + color2.getGreen(), 255);
        int b = Math.min(color1.getBlue() + color2.getBlue(), 255);
        return new Color(r, g, b);
    }

    public static int compute(int a, int b) {
        double normA = a / 255d;
        double normB = b / 255d;
        if (a < 0.5) {
            return (int)(2 * normA * normB * 255);
        }else{
            return (int)((1 - 2 * (1 - normA) * (1 - normB)) * 255);
        }
    }

    public static ColorBitmap subtract(ColorBitmap cb1, ColorBitmap cb2) {
        ColorBitmap sub = new ColorBitmap(cb1.getWidth(), cb1.getHeight());

        for (int x = 0; x < cb1.getWidth(); x++) {
            for (int y = 0; y < cb1.getHeight(); y++) {
                sub.set(x, y, subtract(cb1.at(x, y), cb2.at(x, y)));
            }
        }

        return sub;
    }

    public static Color subtract(Color c1, Color c2) {
        int r = Math.abs(c1.getRed() - c2.getRed());
        int g = Math.abs(c1.getGreen() - c2.getGreen());
        int b = Math.abs(c1.getBlue() - c2.getBlue());
        return new Color(r, g, b);
    }


}
