package workers;

import geometry.Path;
import gui.Controller;
import image.Bitmap;
import image.BitmapLoader;
import image.GrayScaleLoader;
import potrace.BmToPathlist;
import potrace.GetSVG;
import potrace.Info;
import potrace.ProcessPath;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlackAndWhiteWorker extends SwingWorker<Void, String> {
    private BufferedImage img;
    private File svgFile;
    private int scale;
    private int threshold;
    private JTextArea logArea;
    private Info info;

    public BlackAndWhiteWorker(BufferedImage img, File svgFile, int scale, int threshold, JTextArea logArea, Info info) {
        this.img = img;
        this.svgFile = svgFile;
        this.scale = scale;
        this.threshold = threshold;
        this.logArea = logArea;
        this.info = info;
    }

    public BlackAndWhiteWorker(BufferedImage img, File svgFile, int scale, int threshold, JTextArea logArea) {
        this(img, svgFile, scale, threshold, logArea, new Info());
    }

    @Override
    protected Void doInBackground() throws Exception {
        Controller.getInstance().disableAll(true);

        long start = System.currentTimeMillis();
        publish("Image loading...");
        BitmapLoader loader = new GrayScaleLoader(threshold);
        Bitmap bm = loader.load(img);

        publish("Paths extractions...");
        List<Path> pathList = new ArrayList<>();
        BmToPathlist bmToPathlist = new BmToPathlist(bm, info, pathList);
        bmToPathlist.bmToPathlist();

        publish("Paths processing...");
        ProcessPath processPath = new ProcessPath(info, pathList);
        processPath.processPath();

        publish("Svg generation...");
        String svg = GetSVG.getSVG(img.getWidth(), img.getHeight(), scale, pathList, "");

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

}
