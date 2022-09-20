package image;

import image.KMeansExtractor;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class KMeansExtractorTest {

    @Test
    void distanceTest() {
        Color c1 = new Color(7, 4, 3);
        Color c2 = new Color(17, 6, 2);
        double distance = ColorsUtils.distance(c1, c2);
        assertEquals(10.246951, distance, 0.000001);
    }

    @Test
    void computeDistances() {
        Color c1 = new Color(7, 4, 3);
        Color c2 = new Color(17, 6, 2);
        Color[] centroids = {c1, c1, c1, c1};
        Color[] pixels = {c2, c1, c2, c2};
        double[] distances = new double[4];
        KMeansExtractor.computeDistances(distances, centroids, pixels, centroids.length);

        assertEquals(10.246951, distances[0], 0.000001);
        assertEquals(0, distances[1], 0.000001);
        assertEquals(10.246951, distances[2], 0.000001);
        assertEquals(10.246951, distances[3], 0.000001);
    }
}