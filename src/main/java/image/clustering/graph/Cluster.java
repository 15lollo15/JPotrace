package image.clustering.graph;

import java.awt.*;
import java.util.Objects;

public class Cluster {
    private int id;
    private Color color;
    private int size;

    public Cluster(int id) {
        this(id, Color.BLACK, 0);
    }

    public Cluster(int id, Color color, int size) {
        this.id = id;
        this.color = color;
        this.size = size;
    }

    public Cluster mergeWith(Cluster other, int id) {
        double sum = (double) size + other.size;
        double w1 = size / sum;
        double w2 = other.size / sum;
        Color otherColor = other.getColor();
        int r = (int) Math.min(color.getRed() * w1 + otherColor.getRed() * w2, 255);
        int g = (int) Math.min(color.getGreen() * w1 + otherColor.getGreen() * w2, 255);
        int b = (int) Math.min(color.getBlue() * w1 + otherColor.getBlue() * w2, 255);
        Color newColor = new Color(r, g, b);
        return new Cluster(id, newColor, (int)sum);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    private String formatColor() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(color.getRed()).append(", ");
        sb.append(color.getGreen()).append(", ");
        sb.append(color.getBlue()).append(")");
        return sb.toString();
    }

    @Override
    public String toString() {
        return id + ", " + formatColor() + ", " + size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cluster cluster = (Cluster) o;
        return id == cluster.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
