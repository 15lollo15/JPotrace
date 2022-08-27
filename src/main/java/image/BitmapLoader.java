package image;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface BitmapLoader {
    Bitmap load(BufferedImage img);
    Bitmap load(int w, int h, Color[] pixels);

}
