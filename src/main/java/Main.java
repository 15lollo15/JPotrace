import foo.BmToPathlist;
import foo.GetSVG;
import foo.ProcessPath;
import image.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 2)
            System.exit(1);

        String srcImg = args[0];
        String desSvg = args[1];

        BufferedImage img = ImageIO.read(new File(srcImg));
        Bitmap test = new GrayScaleLoader(100).load(img);
        ImageIO.write(test.toBufferedImage(), "bmp", new File("C:\\Users\\Lorenzo\\Desktop\\binary.bmp"));


        Color[] pixels = getPixels(img);


        PaletteExtractor paletteExtractor = new HistogramExtractor(2);

        Set<Color> palette = paletteExtractor.extract(pixels);
        pixels = simplify(pixels, palette);



        int w = img.getWidth();
        int h = img.getHeight();
        ImageIO.write(toBufferedImage(w, h, pixels), "bmp", new File("C:\\Users\\Lorenzo\\Desktop\\simp.bmp"));

        String svg = "<svg id=\"svg\" version=\"1.1\" width=\"" + w + "\" height=\"" + h +
                "\" xmlns=\"http://www.w3.org/2000/svg\">";

        //svg += "<rect width=\""+w+"\" height=\""+h+"\" style=\"fill:rgb(0,0,0);stroke-width:3;stroke:rgb(0,0,0)\" />";

        for (Color c : palette) {
            BitmapLoader loader = new ColorPickerLoader(c);
            Bitmap bm = loader.load(img.getWidth(), img.getHeight(), pixels);
            List<geometry.Path> pathList = new ArrayList<>();

            BmToPathlist bmToPathlist = new foo.BmToPathlist(bm, new foo.Info(), pathList);
            bmToPathlist.bmToPathlist();

            ProcessPath processPath = new ProcessPath(new foo.Info(), pathList);
            processPath.processPath();

            GetSVG getSVG = new GetSVG(1, "", bm, pathList);
            String pathSVG = getSVG.getPath(c);
            svg += pathSVG;


        }
        svg += "</svg>";

        FileWriter fw = new FileWriter(new File("C:\\Users\\Lorenzo\\Desktop\\test\\" + "test" + ".svg"));
        fw.write(svg);
        fw.close();



    }

    public static BufferedImage toBufferedImage(int w, int h, Color[] pixels) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                img.setRGB(x, y, pixels[w * y + x].getRGB());
            }
        }
        return img;
    }

    public static int distance(Color c1, Color c2) {
        int dr = Math.abs(c1.getRed() - c2.getRed());
        int dg = Math.abs(c1.getGreen() - c2.getGreen());
        int db = Math.abs(c1.getBlue() - c2.getBlue());
        return dr + dg + db;
    }

    public static Color[] simplify(Color[] colors, Set<Color> palette) {
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

    public static Color[] getPixels(BufferedImage img) {
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
