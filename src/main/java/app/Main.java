package app;

import com.formdev.flatlaf.FlatLightLaf;
import gui.Controller;
import tracing.conversions.HierarchicalColorConversion;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static final String IMG_PATH = "C:\\Users\\Lorenzo\\Desktop\\goku_small_3.jpg";

    public static void main(String[] args) throws IOException {
//        FlatLightLaf.setup();
//        Controller controller = Controller.getInstance();
//        controller.showWindow();
        BufferedImage img = ImageIO.read(new File(IMG_PATH));
        HierarchicalColorConversion conversion = new HierarchicalColorConversion();
        String svgString = conversion.convert(img);
        FileWriter fw = new FileWriter("C:\\Users\\Lorenzo\\Desktop\\test.svg");
        fw.write(svgString);
        fw.close();
    }

}
