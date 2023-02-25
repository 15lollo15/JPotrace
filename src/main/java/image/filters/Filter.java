package image.filters;

import image.ColorBitmap;

public interface Filter {
    ColorBitmap applyTo(ColorBitmap srcImg);
}
