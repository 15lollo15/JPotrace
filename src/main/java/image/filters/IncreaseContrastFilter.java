package image.filters;

import image.ColorBitmap;

import java.awt.*;

public class IncreaseContrastFilter implements Filter{
    private double contrastK;
    private double brightK;

    public IncreaseContrastFilter(double contrastK, double brightK) {
        this.contrastK = contrastK;
        this.brightK = brightK;
    }

    @Override
    public ColorBitmap applyTo(ColorBitmap srcImg) {
        ColorBitmap newImage = new ColorBitmap(srcImg.getWidth(), srcImg.getHeight());
        for (int x = 0; x < srcImg.getWidth(); x++) {
            for (int y = 0; y < srcImg.getHeight(); y++) {
                Color originalColor = srcImg.at(x, y);
                int r = Math.min((int)(originalColor.getRed() * contrastK + brightK), 255);
                int g = Math.min((int)(originalColor.getGreen() * contrastK + brightK), 255);
                int b = Math.min((int)(originalColor.getBlue() * contrastK + brightK), 255);
                newImage.set(x, y, new Color(r, g, b));
            }
        }
        return newImage;
    }
}
