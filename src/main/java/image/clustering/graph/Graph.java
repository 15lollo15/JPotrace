package image.clustering.graph;

import java.awt.*;
import java.util.*;

public class Graph {
    private Map<Cluster, Set<Link>> adj;
    private int lastId;

    public Graph() {
        adj = new HashMap<>();
        lastId = 0;
    }

    public void addCluster(Cluster c) {
        lastId = Math.max(c.getId(), lastId);
        adj.putIfAbsent(c, new HashSet<>());
    }

    public void addLink(Cluster c1, Cluster c2) {
        double diff = difference(c1.getColor(), c2.getColor());
        Link to2 = new Link(c2, diff);
        Link to1 = new Link(c1, diff);
        Set<Link> adj1 = adj.get(c1);
        Set<Link> adj2 = adj.get(c2);
        adj1.add(to2);
        adj2.add(to1);
    }

    public Cluster merge(Cluster c1, Cluster c2) {
        Set<Link> adj1 = adj.remove(c1);
        Set<Link> adj2 = adj.remove(c2);
        adj1.remove(new Link(c2, 0));
        adj2.remove(new Link(c1, 0));

        Set<Link> adjMerged = new HashSet<>(adj1);
        adjMerged.addAll(adj2);
        Cluster mergedCluster = c1.mergeWith(c2, ++lastId);
        adj.put(mergedCluster, adjMerged);

        removeFromAdj(adj1, c1, mergedCluster);
        removeFromAdj(adj2, c2, mergedCluster);
        return mergedCluster;
    }

    public Cluster getCluster(int id) {
        for (Cluster c : adj.keySet())
            if (c.getId() == id)
                return c;
        return null;
    }

    public Set<Cluster> getClusters() {
        return adj.keySet();
    }

    public Node contract() {
        Set<Cluster> clusters = adj.keySet();
        if (clusters.size() < 2)
            return null;
        Cluster smallerCluster = Collections.min(clusters, Comparator.comparingInt(Cluster::getSize));
        Set<Link> neighbors = adj.get(smallerCluster);
        Link shortestLink = Collections.min(neighbors, Comparator.comparingDouble(Link::getDistance));
        Cluster nearestNeighbor = shortestLink.getNeighbor();
        Cluster merged = merge(smallerCluster, nearestNeighbor);
        return new Node(merged, new Node(smallerCluster), new Node(nearestNeighbor));
    }

    private void removeFromAdj(Set<Link> adj, Cluster toRemove, Cluster toAdd) {
        for (Link l : adj) {
            Cluster neighbor = l.getNeighbor();
            Set<Link> neighborLinks = this.adj.get(neighbor);
            neighborLinks.remove(new Link(toRemove, 0));
            addLink(neighbor, toAdd);
        }
    }

    private static double difference(Color c1, Color c2) {
        double deltaR = Math.pow(c1.getRed() - c2.getRed(), 2);
        double deltaG = Math.pow(c1.getGreen() - c2.getGreen(), 2);
        double deltaB = Math.pow(c1.getBlue() - c2.getBlue(), 2);
        return Math.sqrt(deltaR + deltaG + deltaB);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Cluster, Set<Link>> e : adj.entrySet()) {
            sb.append(e.getKey());
            sb.append(" --> ");
            sb.append(e.getValue());
            sb.append("\n");
        }

        return sb.toString();
    }
}
