package workers;

import geometry.Path;
import gui.Controller;
import image.*;
import potrace.BmToPathlist;
import potrace.GetSVG;
import potrace.Info;
import potrace.ProcessPath;

import javax.swing.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class ColorWorker extends SwingWorker<Void, String> {
    private BufferedImage img;
    private File svgFile;
    private int scale;
    private int numberOfColors;
    private JTextArea logArea;
    private Info info;

    public ColorWorker(BufferedImage img, File svgFile, int scale, int numberOfColors, JTextArea logArea) {
        this(img, svgFile, scale, numberOfColors, logArea, new Info());
    }

    public ColorWorker(BufferedImage img, File svgFile, int scale, int numberOfColors, JTextArea logArea, Info info) {
        this.img = img;
        this.svgFile = svgFile;
        this.scale = scale;
        this.numberOfColors = numberOfColors;
        this.logArea = logArea;
        this.info = info;
    }

    @Override
    protected Void doInBackground() throws Exception {
        Controller.getInstance().disableAll(true);
        long start = System.currentTimeMillis();
        publish("Extract pixels...");
        Color[] pixels = getPixels(img);

        publish("Palette extractions...");
        PaletteExtractor pe = new KMeansExtractor(numberOfColors);
        Set<Color> palette = pe.extract(pixels);

        publish("Pixels simplification...");
        pixels = simplify(pixels, palette);

        Color darkest = darkest(palette);

        publish("Polygons extraction...");
        Map<Color, List<Path>> coloredPath = new HashMap<>();
        for (Color c : palette) {
            publish("\t Extracting (" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")...");
            BitmapLoader loader = new ColorPickerLoader(c);
            Bitmap bm = loader.load(img.getWidth(), img.getHeight(), pixels);

            List<Path> pathList = new ArrayList<>();
            BmToPathlist bmToPathlist = new BmToPathlist(bm, info, pathList);
            bmToPathlist.bmToPathlist();

            ProcessPath processPath = new ProcessPath(info, pathList);
            processPath.processPath();

            coloredPath.put(c, pathList);
        }

        publish("Svg generation...");
        String svg = GetSVG.getSVG(img.getWidth(), img.getHeight(), scale, "", coloredPath, darkest);

        FileWriter fw = new FileWriter(svgFile);
        fw.write(svg);
        fw.close();
        long end = System.currentTimeMillis();

        double time = (end - start) / 1000d;
        publish("COMPLETED in " + time + " seconds");

        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String log : chunks) {
            if (log.equals("-")) {
                logArea.setText("");
                Controller.getInstance().disableAll(true);
                continue;
            }
            logArea.append(log + "\n");
        }
    }

    @Override
    protected void done() {
        super.done();
        Controller.getInstance().disableAll(false);
        JOptionPane.showMessageDialog(Controller.getInstance().getMainFrame(), "Conversion completed");
    }

    private Color darkest(Set<Color> palette) {
        Optional<Color> color = palette.stream().min(this::compareDarkness);
        return color.isPresent() ? color.get() : Color.BLACK;
    }

    private int compareDarkness(Color c1, Color c2) {
        double l1 = luminance(c1);
        double l2 = luminance(c2);
        if (l1 < l2)
            return -1;
        if (l1 > l2)
            return 1;
        return 0;
    }

    private double luminance(Color c) {
        double rNorm = c.getRed() / 255d;
        double gNorm = c.getGreen() / 255d;
        double bNorm = c.getBlue() / 255d;

        double r = Math.pow(rNorm, 2.2);
        double g = Math.pow(gNorm, 2.2);
        double b = Math.pow(bNorm, 2.2);

        r *= 0.2126;
        g *= 0.7152;
        b *= 0.0722;

        return r + g + b;
    }

    private int distance(Color c1, Color c2) {
        int dr = Math.abs(c1.getRed() - c2.getRed());
        int dg = Math.abs(c1.getGreen() - c2.getGreen());
        int db = Math.abs(c1.getBlue() - c2.getBlue());
        return dr + dg + db;
    }

    private Color[] simplify(Color[] colors, Set<Color> palette) {
        Color[] simplifiedColors = new Color[colors.length];

        for (int i = 0; i < simplifiedColors.length; i++) {
            Color color = colors[i];
            Color similar = null;
            for (Color paletteColor : palette) {
                if (similar == null) {
                    similar = paletteColor;
                    continue;
                }
                if (distance(color, paletteColor) < distance(color, similar))
                    similar = paletteColor;
            }
            simplifiedColors[i] = similar;
        }

        return simplifiedColors;
    }

    private Color[] getPixels(BufferedImage img) {
        Color[] pixels = new Color[img.getWidth() * img.getHeight()];
        int k = 0;
        for (int y = 0; y < img.getHeight(); y++)
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                Color color = new Color(rgb);
                pixels[k++] = color;
            }
        return pixels;
    }
}
