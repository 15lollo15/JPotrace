package image.palette;

import utils.ColorsUtils;
import utils.MathUtils;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public class KMeansExtractor implements PaletteExtractor{
    private int k;
    private final Random random;
    private Color[] uniquePixels;
    private int[] numPixels;
    private double inertia;

    public KMeansExtractor (int k) {
        this.k = k;
        random = new Random(15);
    }

    @Override
    public Set<Color> extract(Color[] pixels) {
        generateUniquePixels(pixels);
        Color[] centroids = generateCentroids(uniquePixels);
        int[] cluster = new int[uniquePixels.length];
        int[] oldCluster;

        boolean needRecenter;
        do {
            inertia = 0;
            oldCluster = cluster;
            cluster = new int[uniquePixels.length];
            int[] finalCluster = cluster;
            for (int i = 0; i < uniquePixels.length; i++) {
                finalCluster[i] = minDistance(uniquePixels[i], centroids);
                inertia += ColorsUtils.distance(uniquePixels[i], centroids[finalCluster[i]]) * numPixels[i];
            }
            needRecenter = !Arrays.equals(oldCluster, cluster);
            if (needRecenter)
                recenter(centroids, uniquePixels, cluster, numPixels);
        }while (needRecenter);
        inertia /= pixels.length;

        return Arrays.stream(centroids).collect(Collectors.toSet());
    }

    private void generateUniquePixels(Color[] pixels) {
        Map<Color, Integer> pixelAndColor = new HashMap<>();
        for (Color color : pixels) {
            pixelAndColor.putIfAbsent(color, 0);
            int prevValue = pixelAndColor.get(color);
            pixelAndColor.put(color, prevValue + 1);
        }
        uniquePixels = pixelAndColor.keySet().toArray(new Color[0]);
        numPixels = new int[uniquePixels.length];
        for (int i = 0; i < uniquePixels.length; i++)
            numPixels[i] = pixelAndColor.get(uniquePixels[i]);
    }

    public double getInertia() {
        return inertia;
    }

    private void recenter(Color[] centroids, Color[] pixels, int[] clusters, int[] numPixels) {
        int[] sumsR = new int[k];
        int[] sumsG = new int[k];
        int[] sumsB = new int[k];
        int[] counts = new int[k];

        for (int i = 0; i < clusters.length; i++) {
            int cluster = clusters[i];
            sumsR[cluster] += pixels[i].getRed() * numPixels[i];
            sumsG[cluster] += pixels[i].getGreen() * numPixels[i];
            sumsB[cluster] += pixels[i].getBlue() * numPixels[i];
            counts[cluster] += numPixels[i];
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

    private int minDistance(Color c, Color[] centroids) {
        return minDistance(c, centroids, centroids.length);
    }

    private int minDistance(Color c, Color[] centroids, int l) {
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

    private void computeDistances(double[] distances, Color[] centroids, Color[] pixels, int centroidNumbers) {
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
