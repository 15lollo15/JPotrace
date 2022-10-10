package workers;

import gui.Controller;
import tracing.base.Settings;
import tracing.conversions.KMeansColorConversion;
import utils.ImageUtils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ColorWorker extends SwingWorker<Void, String> {
    private final BufferedImage img;
    private final File svgFile;
    private final int scale;
    private final int numberOfColors;
    private final JTextArea logArea;
    private final int blur;
    private final Settings settings;
    private boolean pixelArt;

    public ColorWorker(BufferedImage img, File svgFile, int scale,
                       int numberOfColors, JTextArea logArea, int blur, Settings settings) {
        this.img = img;
        this.svgFile = svgFile;
        this.scale = scale;
        this.numberOfColors = numberOfColors;
        this.logArea = logArea;
        this.blur = blur;
        this.settings = settings;
    }

    public ColorWorker(BufferedImage img, File svgFile, int scale, JTextArea logArea) {
        this.img = ImageUtils.upscale(img, 10);
        this.svgFile = svgFile;
        this.scale = scale;
        this.numberOfColors = -1;
        this.logArea = logArea;
        this.blur = 1;
        this.settings = new Settings();
        this.settings.setTurdSize(0);
    }

    @Override
    protected Void doInBackground() {
        Controller.getInstance().disableAll(true);
        KMeansColorConversion kMeansColorConversion = new KMeansColorConversion(numberOfColors, blur);
        kMeansColorConversion.setStatusCallback(this::publish);
        String svg = kMeansColorConversion.convert(img);

        try (FileWriter fileWriter = new FileWriter(svgFile)){
            fileWriter.write(svg);
        } catch (IOException e) {
            throw new SVGCreationException();
        }

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
}
