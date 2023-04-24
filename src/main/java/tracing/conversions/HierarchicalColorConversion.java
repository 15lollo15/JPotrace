package tracing.conversions;

import geometry.Path;
import image.BooleanBitmap;
import image.ColorBitmap;
import image.bitmap.loaders.ColorBitmapLoader;
import image.clustering.HoshenKopelman;
import image.clustering.graph.Cluster;
import image.clustering.graph.Graph;
import image.clustering.graph.GraphBuilder;
import image.clustering.graph.Node;
import tracing.base.BooleanBitmapToPathList;
import tracing.base.GetSVG;
import tracing.base.ProcessPath;
import tracing.base.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class HierarchicalColorConversion implements ColorConversion{

    @Override
    public String convert(BufferedImage img, int scale) {
        ColorBitmap bitmap = new ColorBitmapLoader().load(img);
        int[][] clusterMatrix = HoshenKopelman.cluster(bitmap);
        Graph graph = new GraphBuilder().buildFrom(clusterMatrix, bitmap);

        Map<Node, Node> nodes = initNodes(graph.getClusters());

        Node father = graph.contract();
        while (father != null) {
            Node l = nodes.get(father.getLeft());
            Node r = nodes.get(father.getRight());
            nodes.remove(l);
            nodes.remove(r);
            father.setLeft(l);
            father.setRight(r);
            nodes.put(father, father);
            father = graph.contract();
        }

        Node root = nodes.keySet().stream().findFirst().get();
        Map<Node, Set<Node>> children = new HashMap<>();
        getChildren(root, children);

        List<ColorPaths> colorPaths = new ArrayList<>();

        Deque<Node> nodesStack = new ArrayDeque<>();
        nodesStack.push(root);
        boolean isRoot = true;
        int count = 0;

        while (!nodesStack.isEmpty()) {
            Node node = nodesStack.pop();
            if (isRoot) {
                Color color = node.getValue().getColor();
                BooleanBitmap tmpBooleanBitmap = generateBitmap(node, clusterMatrix, children);
                colorPaths.add(0, new ColorPaths(color, extractPathList(tmpBooleanBitmap, count)));
                isRoot = false;
                count++;
            }

            if (node.getLeft() != null) {
                Node left = node.getLeft();
                Color color = left.getValue().getColor();
                BooleanBitmap tmpBooleanBitmap = generateBitmap(left, clusterMatrix, children);
                colorPaths.add(0, new ColorPaths(color, extractPathList(tmpBooleanBitmap, count)));
                nodesStack.push(left);
                count++;
            }

            if (node.getRight() != null) {
                Node right = node.getRight();
                Color color = right.getValue().getColor();
                BooleanBitmap tmpBooleanBitmap = generateBitmap(right, clusterMatrix, children);
                colorPaths.add(0, new ColorPaths(color, extractPathList(tmpBooleanBitmap, count)));
                nodesStack.push(node.getRight());
                count++;
            }
        }

        return GetSVG.getSVG(img.getWidth(), img.getHeight(), scale, "", colorPaths);
    }

    private List<Path> extractPathList(BooleanBitmap tmpBooleanBitmap, int count) {
        BooleanBitmapToPathList booleanBitmapToPathList = new BooleanBitmapToPathList(tmpBooleanBitmap,
                new Settings());
        try {
            ImageIO.write(tmpBooleanBitmap.toBufferedImage(), "png", new File("C:\\Users\\Lorenzo\\Desktop\\out\\" + count + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Path> pathList = booleanBitmapToPathList.toPathList();

        ProcessPath processPath = new ProcessPath(new Settings(), pathList);
        processPath.processPath();
        return pathList;
    }

    private BooleanBitmap generateBitmap(Node root, int[][] matrix, Map<Node, Set<Node>> children) {
        BooleanBitmap img = new BooleanBitmap(matrix[0].length, matrix.length);
        Cluster cluster = root.getValue();
        Set<Node> childrenList = children.get(root);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int clusterId = matrix[y][x];
                Node n = new Node(new Cluster(clusterId));
                img.set(x, y, clusterId == cluster.getId() || childrenList.contains(n));
            }
        }
        return img;
    }

    private void getChildren(Node n, Map<Node, Set<Node>> children) {
        children.putIfAbsent(n, new HashSet<>());
        Set<Node> childrenList = children.get(n);

        if (n.getLeft() != null) {
            childrenList.add(n.getLeft());
            getChildren(n.getLeft(), children);
            childrenList.addAll(children.get(n.getLeft()));
        }

        if (n.getRight() != null) {
            childrenList.add(n.getRight());
            getChildren(n.getRight(), children);
            childrenList.addAll(children.get(n.getRight()));
        }
    }

    private Map<Node, Node> initNodes(Collection<Cluster> clusters) {
        Map<Node, Node> nodes = new HashMap<>();
        for (Cluster c : clusters) {
            Node n = new Node(c);
            nodes.put(n, n);
        }
        return nodes;
    }

    @Override
    public String convert(BufferedImage img) {
        return convert(img, 1);
    }

    @Override
    public void setStatusCallback(Consumer<String> callback) {}
}
