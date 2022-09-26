package image.bitmap.loaders;

import image.BooleanBitmap;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

public class BooleanColorPickerLoader implements BitmapLoader<BooleanBitmap>{
    public static final Color DEFAULT_COLOR = Color.BLACK;
    private List<Color> colorsToPick;

    public BooleanColorPickerLoader() {
        this(DEFAULT_COLOR);
    }

    public BooleanColorPickerLoader(Color colorToPick) {
        this.colorsToPick = List.of(colorToPick);
    }
    public BooleanColorPickerLoader(List<Color> colorsToPick) {
        this.colorsToPick = colorsToPick;
    }

    @Override
    public BooleanBitmap load(BufferedImage img) {
        BooleanBitmap bm = new BooleanBitmap(img.getWidth(), img.getHeight());

        int k = 0;
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                int rgb = img.getRGB(j, i);
                Color c = new Color(rgb);
                bm.set(k++, colorsToPick.contains(c));
            }
        }
        return bm;
    }

    @Override
    public BooleanBitmap load(int w, int h, Color[] pixels) {
        BooleanBitmap bm = new BooleanBitmap(w, h);

        for (int i = 0; i < bm.getSize(); i++) {
            Color c = pixels[i];
            bm.set(i, colorsToPick.contains(c));
        }

        return bm;
    }
}
