package image.clustering.graph;

import java.util.Objects;

public class Link {
    private Cluster neighbor;
    private double distance;

    public Link(Cluster neighbor, double distance) {
        this.neighbor = neighbor;
        this.distance = distance;
    }

    public Cluster getNeighbor() {
        return neighbor;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return neighbor.equals(link.neighbor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(neighbor);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{").append(neighbor).append("}: ").append(distance);

        return sb.toString();
    }
}
