import com.formdev.flatlaf.FlatLightLaf;
import gui.Controller;
import gui.MainFrame;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        FlatLightLaf.setup();
        Controller controller = Controller.getInstance();
        controller.showWindow();
//        if(args.length != 2)
//            System.exit(1);
//
//        File src = new File(args[0]);
//        File dest = new File(args[1]);
//
//        BufferedImage img = ImageIO.read(src);
//        Color[] pixels = getPixels(img);
//
//        PaletteExtractor paletteExtractor = new KMeansExtractor(16);
//        Set<Color> palette = paletteExtractor.extract(pixels);
//
//        pixels = simplify(pixels, palette);
//
//        Color darkest = darkest(palette);
//
//        Map<Color, List<Path>> coloredPath = new HashMap<>();
//        for (Color c : palette) {
//            BitmapLoader loader = new ColorPickerLoader(c);
//            Bitmap bm = loader.load(img.getWidth(), img.getHeight(), pixels);
//
//            List<Path> pathList = new ArrayList<>();
//            BmToPathlist bmToPathlist = new BmToPathlist(bm, new Info(), pathList);
//            bmToPathlist.bmToPathlist();
//
//            ProcessPath processPath = new ProcessPath(new Info(), pathList);
//            processPath.processPath();
//
//            coloredPath.put(c, pathList);
//        }
//
//        String svg = GetSVG.getSVG(img.getWidth(), img.getHeight(), 1, "", coloredPath, darkest);
//
//        FileWriter fw = new FileWriter(dest);
//        fw.write(svg);
//        fw.close();

    }

    public static Color darkest(Set<Color> palette) {
        return palette.stream().min((c1, c2) -> compareDarkness(c1, c2)).get();
    }

    public static int compareDarkness(Color c1, Color c2) {
        double l1 = luminance(c1);
        double l2 = luminance(c2);
        if (l1 < l2)
            return -1;
        if (l1 > l2)
            return 1;
        return 0;
    }

    public static double luminance(Color c) {
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
