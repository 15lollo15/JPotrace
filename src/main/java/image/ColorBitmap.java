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
}
