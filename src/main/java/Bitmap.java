import java.awt.*;
import java.awt.image.BufferedImage;

public class Bitmap {
    private int w;
    private int h;
    private int size;
    private int[] data;

    public Bitmap(BufferedImage img) {
        this(img.getWidth(), img.getHeight());

        int k = 0;
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                int rgb = img.getRGB(j, i);
                Color c = new Color(rgb);
                double color = 0.2126 * c.getRed() + 0.7153 * c.getGreen() +
                        0.0721 * c.getBlue();

                data[k++] = (color < 128 ? 1 : 0);
            }
        }
    }

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

}
