package image.clustering.graph;

import image.ColorBitmap;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GraphBuilder {

    public Graph buildFrom(int[][] clusterMatrix, ColorBitmap img) {
        Map<Integer, ClusterBuilder> clusterBuilders = new HashMap<>();
        Map<Integer, Integer> idLinks = new HashMap<>();
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color pixelColor = img.at(x, y);
                int clusterID = clusterMatrix[y][x];
                clusterBuilders.putIfAbsent(clusterID, new ClusterBuilder());
                clusterBuilders.get(clusterID).addColor(pixelColor);

                int topCluster = -1;
                if ((y-1) >= 0)
                    topCluster = clusterMatrix[y-1][x];

                int leftCluster = -1;
                if ((x-1) >= 0)
                    leftCluster = clusterMatrix[y][x-1];

                if (topCluster != -1 && topCluster != clusterID)
                    idLinks.put(topCluster, clusterID);

                if (leftCluster != -1 && leftCluster != clusterID)
                    idLinks.put(leftCluster, clusterID);
            }
        }

        Graph g = new Graph();
        for (Map.Entry<Integer, ClusterBuilder> e : clusterBuilders.entrySet()) {
            int id = e.getKey();
            Cluster c = e.getValue().generateCluster(id);
            g.addCluster(c);
        }

        for (Map.Entry<Integer, Integer> e : idLinks.entrySet()) {
            int fromId = e.getKey();
            int toId = e.getValue();
            Cluster from = clusterBuilders.get(fromId).generateCluster(fromId);
            Cluster to = clusterBuilders.get(toId).generateCluster(toId);
            g.addLink(from, to);
        }

        return g;
    }
}
