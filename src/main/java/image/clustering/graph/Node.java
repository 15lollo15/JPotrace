package image.clustering.graph;

import java.util.Objects;

public class Node {
    private Cluster value;
    private Node left;
    private Node right;

    public Node(Cluster value) {
        this(value, null ,null);
    }

    public Node(Cluster value, Node left, Node right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public Cluster getValue() {
        return value;
    }

    public void setValue(Cluster value) {
        this.value = value;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(value);
        sb.append("\n\t");
        if (left != null)
            sb.append(left.getValue());
        else
            sb.append("null");
        sb.append("\n\t");
        if (right != null)
            sb.append(right.getValue());
        else
            sb.append("null");
        return sb.toString();
    }

    public void print() {
        print(0);
    }

    private void print(int numTab) {
        for (int i = 0; i < numTab; i++)
            System.out.print("\t");
        System.out.println(value.getId());
        if (left != null)
            left.print(numTab + 1);
        if (right != null)
            right.print(numTab + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return value.equals(node.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
