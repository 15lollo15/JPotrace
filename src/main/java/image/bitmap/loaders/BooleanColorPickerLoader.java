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
    public BooleanBitmap load(int w, int h, Color[] pixels) {
        BooleanBitmap bm = new BooleanBitmap(w, h);
        int[] data = bm.getData();

        for (int i = 0; i < data.length; i++) {
            Color c = pixels[i];
            data[i] = (colorsToPick.contains(c) ? 1 : 0);
        }

        return bm;
    }
}
