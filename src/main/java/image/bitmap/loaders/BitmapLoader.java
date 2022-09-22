package image.bitmap.loaders;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface BitmapLoader <T>{
    T load(BufferedImage img);
    T load(int w, int h, Color[] pixels);
}
