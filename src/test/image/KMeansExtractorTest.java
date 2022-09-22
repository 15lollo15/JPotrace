package image;

import org.junit.jupiter.api.Test;
import utils.ColorsUtils;

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

}