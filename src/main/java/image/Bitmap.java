package image;

import java.awt.image.BufferedImage;

public interface Bitmap <T>{
    T at(int x, int y);
    T at(int i);
    int getWidth();
    int getHeight();
    int getSize();
    void set(int x, int y, T value);
    void set(int i, T value);
    BufferedImage toBufferedImage();
}
