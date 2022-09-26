package workers;

import geometry.Path;
import gui.Controller;
import image.*;
import image.bitmap.loaders.BooleanColorPickerLoader;
import image.bitmap.loaders.ColorBitmapLoader;
import image.palette.KMeansExtractor;
import image.palette.PaletteExtractor;
import potrace.BooleanBitmapToPathList;
import potrace.GetSVG;
import potrace.Info;
import potrace.ProcessPath;
import utils.ColorsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ColorWorker extends SwingWorker<Void, String> {
    private final BufferedImage img;
    private final File svgFile;
    private final int scale;
    private final int numberOfColors;
    private final JTextArea logArea;
    private final int blur;
    private final Info info;

    public ColorWorker(BufferedImage img, File svgFile, int scale,
                       int numberOfColors, JTextArea logArea, int blur, Info info) {
        this.img = img;
        this.svgFile = svgFile;
        this.scale = scale;
        this.numberOfColors = numberOfColors;
        this.logArea = logArea;
        this.blur = blur;
        this.info = info;
    }

    @Override
    protected Void doInBackground() {
        Controller.getInstance().disableAll(true);
        long start = System.currentTimeMillis();
        ColorBitmap bitmap = new ColorBitmapLoader().load(img);

        publish("Blurring...");
        bitmap = Filters.blur(bitmap, blur);

        publish("Extract pixels...");
        Color[] pixels = bitmap.getData();

        publish("Palette extractions...");
        PaletteExtractor pe = new KMeansExtractor(numberOfColors);
        Set<Color> palette = pe.extract(pixels);
        pixels = simplify(pixels, palette);

        cleanPalette(palette, pixels, 17);

        publish("Pixels simplification...");
        pixels = simplify(pixels, palette);

        publish("Polygons extraction...");
        Color[] colors = new Color[palette.size()];
        List<Path>[] paths = new ArrayList[palette.size()];
        List<Color> colorsToPick = new ArrayList<>();
        int index = 0;
        for (Color c : palette) {
            colorsToPick.add(c);
            publish("\t Extracting (" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")...");
            BooleanColorPickerLoader loader = new BooleanColorPickerLoader(colorsToPick);
            BooleanBitmap bm = loader.load(img.getWidth(), img.getHeight(), pixels);

            BooleanBitmapToPathList booleanBitmapToPathlist = new BooleanBitmapToPathList(bm, info);
            List<Path> pathList = booleanBitmapToPathlist.toPathList();

            ProcessPath processPath = new ProcessPath(info, pathList);
            processPath.processPath();

            colors[index] = c;
            paths[index] = pathList;
            index++;
        }

        publish("Svg generation...");
        String svg = GetSVG.getSVG(img.getWidth(), img.getHeight(), scale, "", colors, paths);

        try (FileWriter fileWriter = new FileWriter(svgFile)){
            fileWriter.append(svg);
        }catch (IOException e) {
            throw new SVGCreationException();
        }
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
                if (ColorsUtils.distance(color, paletteColor) < ColorsUtils.distance(color, similar))
                    similar = paletteColor;
            }
            simplifiedColors[i] = similar;
        }

        return simplifiedColors;
    }

    private Map<Color, Integer> countPixelsForColor(Color[] pixels) {
        Map<Color, Integer> pixelsForColor = new HashMap<>();

        for (Color pixel : pixels) {
            pixelsForColor.computeIfAbsent(pixel, p -> 0);
            int oldValue = pixelsForColor.get(pixel);
            pixelsForColor.put(pixel, oldValue + 1);
        }

        return pixelsForColor;
    }

    private void cleanPalette(Set<Color> palette, Color[] pixels, double threshold) {
        Map<Color, Integer> pixelsForColor =  countPixelsForColor(pixels);
        List<Color> colors = palette.stream().toList();
        for (int i = 0; i < colors.size(); i++) {
            Color c1 = colors.get(i);
            if (!palette.contains(c1)) continue;
            for (int j = i + 1; j < colors.size(); j++) {
                Color c2 = colors.get(j);
                if (palette.contains(c2)) {
                    double diff = ColorsUtils.distance(c1, c2);

                    if (diff >= threshold)
                        continue;

                    int numPixels1 = pixelsForColor.get(c1);
                    int numPixels2 = pixelsForColor.get(c2);

                    if (numPixels1 > numPixels2)
                        palette.remove(c2);
                    else
                        palette.remove(c1);
                }
            }
        }
    }
}
