package image;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class KMeansExtractor implements PaletteExtractor{
    private int k;
    private Random random;

    public KMeansExtractor (int k) {
        this.k = k;
        random = new Random();
    }

    @Override
    public Set<Color> extract(Color[] pixels) {
        Color[] centroids = generateCentroids(pixels);
        int[] cluster = new int[pixels.length];
        int[] oldCluster;

        boolean needRecenter = false;
        do {
            oldCluster = cluster;
            cluster = new int[pixels.length];
            for (int i = 0; i < pixels.length; i++) {
                cluster[i] = minDistance(pixels[i], centroids);
            }
            needRecenter = !Arrays.equals(oldCluster, cluster);
            if (needRecenter)
                recenter(centroids, pixels, cluster);
        }while (needRecenter);

        return Arrays.stream(centroids).collect(Collectors.toSet());
    }

    private void recenter(Color[] centroids, Color[] pixels, int[] clusters) {
        int[] sumsR = new int[k];
        int[] sumsG = new int[k];
        int[] sumsB = new int[k];
        int[] counts = new int[k];

        for (int i = 0; i < clusters.length; i++) {
            int cluster = clusters[i];
            sumsR[cluster] += pixels[i].getRed();
            sumsG[cluster] += pixels[i].getGreen();
            sumsB[cluster] += pixels[i].getBlue();
            counts[cluster]++;
        }

        for (int i = 0; i < centroids.length; i++) {
            if (counts[i] == 0) {
                centroids[i] = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                continue;
            }

            int r = sumsR[i] / counts[i];
            int g = sumsG[i] / counts[i];
            int b = sumsB[i] / counts[i];
            centroids[i] =  new Color(r, g, b);
        }
    }

    public static int minDistance(Color c, Color[] centroids) {
        return minDistance(c, centroids, centroids.length);
    }

    public static int minDistance(Color c, Color[] centroids, int l) {
        int minIndex = 0;
        for (int i = 1; i < l; i++) {
            Color minColor = centroids[minIndex];
            Color tmp = centroids[i];
            if (distance(c, minColor) > distance(c, tmp))
                minIndex = i;
        }
        return minIndex;
    }

    public static double distance(Color c1, Color c2) {
        double rQuad = square(c1.getRed() - c2.getRed());
        double gQuad = square(c1.getGreen() - c2.getGreen());
        double bQuad = square(c1.getBlue() - c2.getBlue());
        return Math.sqrt(rQuad + gQuad + bQuad);
    }

    public static double square(double x) {
        return x * x;
    }

    private Color[] generateRandomCentroids() {
        Color[] centroids = new Color[k];
        for (int i = 0; i < k; i++) {
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            centroids[i] = new Color(r, g, b);
        }
        return centroids;
    }

    private Color[] generateCentroids(Color[] pixels) {
        Color[] centroids = new Color[k];
        centroids[0] = pixels[random.nextInt(pixels.length)];
        double[] distances = new double[pixels.length];
        double[] distancesSums = new double[pixels.length];

        computeDistancesFromFirstCentroid(distances, centroids, pixels);
        computeWeights(distances, distancesSums);

        for (int i = 1; i < k; i++) {
            double last = distancesSums[distancesSums.length - 1];
            double r = random.nextDouble() * last;
            int centroidIndex = -1;
            for (int j = 0; j < distancesSums.length; j++)
                if (distancesSums[j] >= r) {
                    centroidIndex = j;
                    break;
                }
            centroids[i] = pixels[centroidIndex];

            try {
                computeDistances(distances, centroids, pixels, i + 1);
            }catch (RuntimeException e) {
                k = i + 1;
                centroids = Arrays.copyOfRange(centroids, 0, k);
                break;
            }

            computeWeights(distances, distancesSums);
        }

        return centroids;
    }

    private void computeDistancesFromFirstCentroid(double[] distances, Color[] centroids, Color[] pixels) {
        for (int i = 0; i < distances.length; i++) {
            distances[i] = distance(centroids[0], pixels[i]);
        }
    }

    public static void computeDistances(double[] distances, Color[] centroids, Color[] pixels, int centroidNumbers) {
        boolean allZero = true;
        for (int l = 0; l < distances.length; l++) {
            int minDistanceIndex = minDistance(pixels[l], centroids, centroidNumbers);
            distances[l] = distance(pixels[l], centroids[minDistanceIndex]);
            if (allZero && distances[l] != 0)
                allZero = false;
        }
        if (allZero)
            throw new RuntimeException("Too many k");
    }

    private void computeWeights(double[] distances, double[] distancesSums) {
        double max = square(Arrays.stream(distances).max().getAsDouble());
        for (int i = 0; i < distances.length; i++) {
            double distance = square(distances[i]) / max;
            distancesSums[i] = distance;
            if (i != 0)
                distancesSums[i] += distancesSums[i - 1];
        }
    }
}
