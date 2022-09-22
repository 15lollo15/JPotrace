package workers;

import geometry.Path;
import gui.Controller;
import image.*;
import image.bitmap.loaders.BooleanColorPickerLoader;
import image.bitmap.loaders.ColorBitmapLoader;
import image.palette.KMeansExtractor;
import image.palette.PaletteExtractor;
import potrace.BmToPathlist;
import potrace.GetSVG;
import potrace.Info;
import potrace.ProcessPath;

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

            List<Path> pathList = new ArrayList<>();
            BmToPathlist bmToPathlist = new BmToPathlist(bm, info, pathList);
            bmToPathlist.bmToPathlist();

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

}
