package workers;

import gui.Controller;
import tracing.base.Settings;
import tracing.conversions.BinaryConversion;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class BlackAndWhiteWorker extends SwingWorker<Void, String> {
    private final BufferedImage img;
    private final File svgFile;
    private final int scale;
    private final int threshold;
    private final JTextArea logArea;
    private final Settings settings;

    public BlackAndWhiteWorker(BufferedImage img, File svgFile, int scale,
                               int threshold, JTextArea logArea, Settings settings) {
        this.img = img;
        this.svgFile = svgFile;
        this.scale = scale;
        this.threshold = threshold;
        this.logArea = logArea;
        this.settings = settings;
    }

    public BlackAndWhiteWorker(BufferedImage img, File svgFile, int scale, int threshold,JTextArea logArea) {
        this(img, svgFile, scale, threshold, logArea, new Settings());
    }

    @Override
    protected Void doInBackground() {
        Controller.getInstance().disableAll(true);
        BinaryConversion conversion = new BinaryConversion(threshold);
        conversion.setStatusCallback(this::publish);
        String svg = conversion.convert(img, scale);

        try (FileWriter fileWriter = new FileWriter(svgFile);){
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
