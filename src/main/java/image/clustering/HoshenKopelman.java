package image.clustering;

import image.ColorBitmap;
import utils.ColorsUtils;

import java.awt.*;

public class HoshenKopelman {
    public static final int THRESHOLD = 25;
    private HoshenKopelman(){}

    public static int[][] cluster(ColorBitmap img) {
        int height = img.getHeight();
        int width = img.getWidth();
        int[][] matrix = new int[height][width];
        int lastClusterNumber = 1;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color pixel = img.at(x, y);
                Color upPixel = null;
                if ((y - 1) >= 0)
                    upPixel = img.at(x, y - 1);

                Color leftPixel = null;
                if ((x - 1) >= 0)
                    leftPixel =img.at(x - 1, y);

                boolean isSimilarUp = isSimilar(pixel, upPixel);
                boolean isSimilarLeft = isSimilar(pixel, leftPixel);
                lastClusterNumber = computeClusterAt(x, y, matrix, isSimilarUp, isSimilarLeft, lastClusterNumber);
            }
        }

        return matrix;
    }

    public static int computeClusterAt(int x, int y, int[][] matrix, boolean isSimilarUp, boolean isSimilarLeft, int lastClusterNumber) {
        if (!isSimilarUp && !isSimilarLeft) {
            lastClusterNumber++;
            matrix[y][x] = lastClusterNumber;
        }else if (isSimilarUp && !isSimilarLeft) {
            matrix[y][x] = matrix[y - 1][x];
        }else if (!isSimilarUp) {
            matrix[y][x] = matrix[y][x - 1];
        }else {
            if (matrix[y - 1][x] != matrix[y][x - 1]) {
                int minClusterNumber = Math.min(matrix[y - 1][x], matrix[y][x - 1]);
                int maxClusterNumber = Math.max(matrix[y - 1][x], matrix[y][x - 1]);
                substitute(matrix, maxClusterNumber, minClusterNumber);
            }
            matrix[y][x] = matrix[y - 1][x];
        }
        return lastClusterNumber;
    }

    public static void substitute(int[][] m, int oldValue, int newValue) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                if (m[i][j] == oldValue)
                    m[i][j] = newValue;
            }
        }
    }

    public static boolean isSimilar(Color c1, Color c2) {
        if (c1 == null || c2 == null)
            return  false;
        double difference = ColorsUtils.distance(c1, c2);
        return difference < THRESHOLD;
    }

}
