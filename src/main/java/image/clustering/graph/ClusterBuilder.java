package image.clustering.graph;

import java.awt.*;

public class ClusterBuilder {
    private int sumR;
    private int sumG;
    private int sumB;
    private int count;

    public ClusterBuilder() {
        sumR = 0;
        sumG = 0;
        sumB = 0;
        count = 0;
    }

    public void addColor(Color c) {
        sumR += c.getRed();
        sumG += c.getGreen();
        sumB += c.getBlue();
        count++;
    }

    public Cluster generateCluster(int id) {
        int r = sumR / count;
        int g = sumG / count;
        int b = sumB / count;
        Color c = new Color(r, g, b);
        return new Cluster(id, c, count);
    }
}
