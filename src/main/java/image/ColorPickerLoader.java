package image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorPickerLoader implements BitmapLoader{
    public static final Color DEFAULT_COLOR = Color.BLACK;
    private Color colorToPick;

    public ColorPickerLoader() {
        this(DEFAULT_COLOR);
    }

    public ColorPickerLoader(Color colorToPick) {
        this.colorToPick = colorToPick;
    }

    @Override
    public Bitmap load(BufferedImage img) {
        Bitmap bm = new Bitmap(img.getWidth(), img.getHeight());
        int[] data = bm.getData();

        int k = 0;
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                int rgb = img.getRGB(j, i);
                Color c = new Color(rgb);
                data[k++] = (c.equals(colorToPick) ? 1 : 0);
            }
        }
        return bm;
    }

    @Override
    public Bitmap load(int w, int h, Color[] pixels) {
        Bitmap bm = new Bitmap(w, h);
        int[] data = bm.getData();

        for (int i = 0; i < data.length; i++) {
            Color c = pixels[i];
            data[i] = (c.equals(colorToPick) ? 1 : 0);
        }

        return bm;
    }
}
