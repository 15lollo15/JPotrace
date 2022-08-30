package image;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

public class HistogramExtractor implements PaletteExtractor {
    private int partitions;

    public HistogramExtractor(int partitions) {
        this.partitions = partitions;
    }

    @Override
    public Set<Color> extract(Color[] pixels) {
        int partitionSize = 256 / partitions;
        List<Color>[][][] buckets = new List[partitions][partitions][partitions];
        for (int i = 0; i < pixels.length; i++) {
            int rgb = pixels[i].getRGB();
            Color c = new Color(rgb);
            int r = c.getRed() / partitionSize;
            int g = c.getGreen() / partitionSize;
            int b = c.getBlue() / partitionSize;

            r = r < partitions ? r : partitions - 1;
            g = g < partitions ? g : partitions - 1;
            b = b < partitions ? b : partitions - 1;

            if (buckets[r][g][b] == null)
                buckets[r][g][b] = new ArrayList<>();
            buckets[r][g][b].add(c);
        }
        Color[] avgs = bucketsAvgs(buckets);
        return Arrays.stream(avgs).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private Color[] bucketsAvgs(List<Color>[][][] buckets) {
        Color[] avgs = new Color[partitions*partitions*partitions];
        int k = 0;
        for (int r = 0; r < buckets.length; r++) {
            for (int g = 0; g < buckets.length; g++) {
                for (int b = 0; b < buckets.length; b++) {
                    List<Color> colors = buckets[r][g][b];
                    if (colors == null) {
                        avgs[k++] = null;
                    }else{
                        avgs[k++] = avg(colors);
                    }
                }
            }
        }
        return avgs;
    }

    private Color avg(List<Color> colors) {
        int rSum = 0;
        int gSum = 0;
        int bSum = 0;
        for (Color color : colors) {
            rSum += color.getRed();
            gSum += color.getGreen();
            bSum += color.getBlue();
        }
        int r = rSum / colors.size();
        int g = gSum / colors.size();
        int b = bSum / colors.size();

        return new Color(r, g, b);
    }
}
