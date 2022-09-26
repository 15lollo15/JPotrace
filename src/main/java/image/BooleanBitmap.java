package image;

import geometry.IntegerPoint;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BooleanBitmap implements Bitmap<Boolean>{
    private int w;
    private int h;
    private int size;
    private boolean[] data;

    public BooleanBitmap(int w, int h) {
        this.w = w;
        this.h = h;
        this.size = w * h;
        this.data = new boolean[this.size];
    }

    @Override
    public Boolean at(int x, int y) {
        return (x >= 0 && x < this.w && y >=0 && y < this.h) &&
                this.data[this.w * y + x];
    }

    @Override
    public Boolean at(int i) {
        return (i >= 0 && i < data.length) && data[i];
    }

    @Override
    public void set(int x, int y, Boolean value) {
        this.data[this.w * y + x] = value;
    }

    @Override
    public void set(int i, Boolean value) {
        data[i] = value;
    }

    public IntegerPoint index(int i) {
        IntegerPoint point = new IntegerPoint();
        point.setY(i / this.w);
        point.setX(i - point.getY() * this.w);
        return point;
    }

    public void flip(int x, int y) {
        if (this.at(x, y)) {
            this.data[this.w * y + x] = false;
        } else {
            this.data[this.w * y + x] = true;
        }
    }

    public BooleanBitmap copy() {
        BooleanBitmap bm = new BooleanBitmap(this.w, this.h);
        if (this.size >= 0)
            System.arraycopy(this.data, 0, bm.data, 0, this.size);
        return bm;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getWidth() {
        return w;
    }

    @Override
    public int getHeight() {
        return h;
    }

    @Override
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
