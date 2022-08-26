import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

public class Potrace {
    public Bitmap bm;

    public void loadBm(BufferedImage img) {
        bm = new Bitmap(img.getWidth(), img.getHeight());

        int k = 0;
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                int rgb = img.getRGB(j, i);
                Color c = new Color(rgb);
                double color = 0.2126 * c.getRed() + 0.7153 * c.getGreen() +
                        0.0721 * c.getBlue();

                bm.data[k++] = (color < 128 ? 1 : 0);
            }
        }
    }

    public void process() {

    }


}
