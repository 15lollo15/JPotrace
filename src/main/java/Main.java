import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 2)
            System.exit(1);

        String srcImg = args[0];
        String desSvg = args[1];

        BufferedImage img = ImageIO.read(new File(srcImg));

        Bitmap bm = new Bitmap(img);

        List<Path> pathList = new ArrayList<>();
        BmToPathlist bmToPathlist = new BmToPathlist(bm, new Info(), pathList);
        bmToPathlist.bmToPathlist();

        ProcessPath processPath = new ProcessPath(new Info(), pathList);
        processPath.processPath();

        GetSVG getSVG = new GetSVG(1, "ciao", bm, pathList);
        String svg = getSVG.getSVG();

        FileWriter fw = new FileWriter(desSvg);
        fw.write(svg);
        fw.close();

    }

}
