package image;

import java.awt.*;
import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KMeansExtractor implements PaletteExtractor{
    private int k;
    private final Random random;

    public KMeansExtractor (int k) {
        this.k = k;
        random = new Random();
    }

    @Override
    public Set<Color> extract(Color[] pixels) {
        Color[] centroids = generateCentroids(pixels);
        int[] cluster = new int[pixels.length];
        int[] oldCluster;

        boolean needRecenter;
        do {
            oldCluster = cluster;
            cluster = new int[pixels.length];
            int[] finalCluster = cluster;
            IntStream.range(0, pixels.length).parallel().forEach(i ->
                finalCluster[i] = minDistance(pixels[i], centroids));
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
            double distanceFromMinimum = ColorsUtils.distance(c, minColor);
            double distanceFromTmp = ColorsUtils.distance(c, tmp);
            if (distanceFromMinimum > distanceFromTmp)
                minIndex = i;
        }
        return minIndex;
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
            distances[i] = ColorsUtils.distance(centroids[0], pixels[i]);
        }
    }

    public static void computeDistances(double[] distances, Color[] centroids, Color[] pixels, int centroidNumbers) {
        boolean allZero = true;
        for (int l = 0; l < distances.length; l++) {
            int minDistanceIndex = minDistance(pixels[l], centroids, centroidNumbers);
            distances[l] = ColorsUtils.distance(pixels[l], centroids[minDistanceIndex]);
            if (allZero && distances[l] != 0)
                allZero = false;
        }
        if (allZero)
            throw new TooManyColorsException();
    }

    private void computeWeights(double[] distances, double[] distancesSums) {
        OptionalDouble optMax = Arrays.stream(distances).max();
        if(optMax.isEmpty())
            throw new MaxNotFoundException();
        double max = MathUtils.square(Arrays.stream(distances).max().getAsDouble());
        for (int i = 0; i < distances.length; i++) {
            double distance = MathUtils.square(distances[i]) / max;
            distancesSums[i] = distance;
            if (i != 0)
                distancesSums[i] += distancesSums[i - 1];
        }
    }

    public static class TooManyColorsException extends RuntimeException{}
    public static class MaxNotFoundException extends RuntimeException{}
}
