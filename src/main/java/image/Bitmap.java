package image;

import geometry.Point;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Bitmap {
    private int w;
    private int h;
    private int size;
    private int[] data;

    public Bitmap(int w, int h) {
        this.w = w;
        this.h = h;
        this.size = w * h;
        this.data = new int[this.size];
    }

    public boolean at(int x, int y) {
        return (x >= 0 && x < this.w && y >=0 && y < this.h) &&
                this.data[this.w * y + x] == 1;
    }

    public void set(int x, int y, boolean value) {
        this.data[this.w * y + x] = value ? 1 : 0;
    }

    public Point index(int i) {
        Point point = new Point();
        point.setY(i / this.w);
        point.setX(i - point.getY() * this.w);
        return point;
    }

    public void flip(int x, int y) {
        if (this.at(x, y)) {
            this.data[this.w * y + x] = 0;
        } else {
            this.data[this.w * y + x] = 1;
        }
    }

    public Bitmap copy() {
        Bitmap bm = new Bitmap(this.w, this.h);
        if (this.size >= 0)
            System.arraycopy(this.data, 0, bm.data, 0, this.size);
        return bm;
    }

    public int[] getData() {
        return data;
    }

    public int getSize() {
        return size;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public BufferedImage toBufferedImage() {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (at(x, y))
                    img.setRGB(x, y, Color.BLACK.getRGB());
                else
                    img.setRGB(x, y, Color.WHITE.getRGB());
            }
        }

        return img;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (at(x, y))
                    builder.append("##");
                else
                    builder.append("  ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
