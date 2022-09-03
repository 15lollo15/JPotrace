package image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

public class ColorPickerLoader implements BitmapLoader{
    public static final Color DEFAULT_COLOR = Color.BLACK;
    private List<Color> colorsToPick;

    public ColorPickerLoader() {
        this(DEFAULT_COLOR);
    }

    public ColorPickerLoader(Color colorToPick) {
        this.colorsToPick = List.of(colorToPick);
    }
    public ColorPickerLoader(List<Color> colorsToPick) {
        this.colorsToPick = colorsToPick;
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
                data[k++] = (colorsToPick.contains(c) ? 1 : 0);
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
            data[i] = (colorsToPick.contains(c) ? 1 : 0);
        }

        return bm;
    }
}
