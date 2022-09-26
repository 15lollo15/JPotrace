package image;

import utils.MathUtils;

import java.awt.*;

public class Filters {
    private Filters() {}

    public static ColorBitmap increaseContrast(ColorBitmap img, double contrastK, double brightK) {
        ColorBitmap newImage = new ColorBitmap(img.getWidth(), img.getHeight());
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color originalColor = img.at(x, y);
                int r = Math.min((int)(originalColor.getRed() * contrastK + brightK), 255);
                int g = Math.min((int)(originalColor.getGreen() * contrastK + brightK), 255);
                int b = Math.min((int)(originalColor.getBlue() * contrastK + brightK), 255);
                newImage.set(x, y, new Color(r, g, b));
            }
        }
        return newImage;
    }

    public static ColorBitmap blur(ColorBitmap img, int kernelSize) {
        if (kernelSize % 2 == 0) return null;
        if (kernelSize == 1) return img.copy();
        ColorBitmap blurredImg = new ColorBitmap(img.getWidth(), img.getHeight());
        double[][] gauss = gaussMatrix(kernelSize);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                computeBlurAt(x, y, gauss, img, blurredImg);
            }
        }
        return blurredImg;
    }

    private static void computeBlurAt(int x, int y, double[][] gauss, ColorBitmap img, ColorBitmap blurredImg) {
        int kernelSize = gauss.length;
        double sumR = 0;
        double sumG = 0;
        double sumB = 0;
        double n = 0;
        for (int dx = - (kernelSize /2); dx <= (kernelSize /2); dx++) {
            for (int dy = - (kernelSize /2); dy <= (kernelSize /2); dy++) {
                int newX = x + dx;
                int newY = y + dy;
                if (newX < 0 || newX >= img.getWidth() || newY < 0 || newY >= img.getHeight()) continue;
                Color c = img.at(x + dx, y + dy);
                double weight = gauss[dy + (kernelSize /2)][dx + (kernelSize /2)];
                sumR += c.getRed() * weight;
                sumG += c.getGreen() * weight;
                sumB += c.getBlue() * weight;
                n += weight;
            }
        }
        if (n == 0)
            return;
        int r = (int)(sumR / n);
        int g = (int)(sumG / n);
        int b = (int)(sumB / n);
        blurredImg.set(x, y, new Color(r, g, b));
    }

    public static double[][] gaussMatrix(int size) {
        double[][] matrix = new double[size][size];
        int range = size / 2;
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <=range; y++) {
                matrix[y+range][x+range] = gauss(x, y, computeSigma(size));
            }
        }

        return matrix;
    }

    public static double computeSigma(int size) {
        return Math.pow(2, size) / (size * Math.sqrt(2 * Math.PI) + (size/2d) * Math.sqrt(2 * Math.PI));
    }

    public static double gauss(int x, int y, double sigma) {
        double partial = 1 / (2 * Math.PI * MathUtils.square(sigma));
        double exp = (MathUtils.square(x) + MathUtils.square(y)) / (2 * MathUtils.square(sigma));
        return partial * (1 / Math.pow(Math.E, exp));
    }

}
