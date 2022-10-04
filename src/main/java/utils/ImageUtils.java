package utils;

import java.awt.image.BufferedImage;

public class ImageUtils {
    private ImageUtils() {}

    public static BufferedImage upscale(BufferedImage img, int factor) {
        int width = img.getWidth() * factor;
        int height = img.getHeight() * factor;
        BufferedImage scaledImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                for (int dx = x * factor; dx < (x*factor) + factor; dx++) {
                    for (int dy = y * factor; dy < (y*factor) + factor; dy++)
                        scaledImg.setRGB(dx, dy, rgb);
                }
            }
        }
        return scaledImg;
    }
}
