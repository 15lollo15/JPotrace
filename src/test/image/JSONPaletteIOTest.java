package image;

import image.palette.JSONPaletteIO;
import image.palette.PaletteIO;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JSONPaletteIOTest {


    @Test
    void exportPaletteHex() throws IOException {
        Set<Color> palette = getPalette();
        File testFile = new File("out_test.json");

        JSONPaletteIO paletteIO = new JSONPaletteIO();
        paletteIO.exportPalette(PaletteIO.PaletteType.HEX, palette, testFile);

        BufferedReader fileReader = new BufferedReader(new FileReader(testFile));

        String str = "";
        String line = fileReader.readLine();
        while (line != null) {
            str = str.concat(line);
            line = fileReader.readLine();
        }

        String correctJson = "{\"palette\":[{\"h\":\"#fc03fc\"},{\"h\":\"#03fcb1\"},{\"h\":\"#fcba03\"}],\"type\":\"HEX\"}";
        assertEquals(correctJson, str);
    }

    @Test
    void exportPaletteRGB() throws IOException {
        Set<Color> palette = getPalette();
        File testFile = new File("out_test.json");

        JSONPaletteIO paletteIO = new JSONPaletteIO();
        paletteIO.exportPalette(PaletteIO.PaletteType.RGB, palette, testFile);

        BufferedReader fileReader = new BufferedReader(new FileReader(testFile));

        String str = "";
        String line = fileReader.readLine();
        while (line != null) {
            str = str.concat(line);
            line = fileReader.readLine();
        }

        String correctJson = "{\"palette\":[{\"r\":252,\"b\":252,\"g\":3}," +
                "{\"r\":3,\"b\":177,\"g\":252},{\"r\":252,\"b\":3,\"g\":186}],\"type\":\"RGB\"}";
        assertEquals(correctJson, str);
    }

    @Test
    void importPaletteHex() throws IOException {
        String correctJson = "{\"palette\":[{\"h\":\"#fc03fc\"},{\"h\":\"#03fcb1\"},{\"h\":\"#fcba03\"}],\"type\":\"HEX\"}";
        File testFile = new File("in_test.json");
        FileWriter fw = new FileWriter(testFile);
        fw.write(correctJson);

        JSONPaletteIO paletteIO = new JSONPaletteIO();
        Set<Color> palette = paletteIO.importPalette(testFile);

        assertEquals(3, palette.size());
        assertTrue(palette.contains(new Color(252, 186, 3)));
        assertTrue(palette.contains(new Color(3, 252, 177)));
        assertTrue(palette.contains(new Color(252, 3, 252)));
    }

    @Test
    void importPaletteRGB() throws IOException {
        String correctJson = "{\"palette\":[{\"r\":252,\"b\":252,\"g\":3}," +
                "{\"r\":3,\"b\":177,\"g\":252},{\"r\":252,\"b\":3,\"g\":186}],\"type\":\"RGB\"}";        File testFile = new File("in_test.json");
        FileWriter fw = new FileWriter(testFile);
        fw.write(correctJson);

        JSONPaletteIO paletteIO = new JSONPaletteIO();
        Set<Color> palette = paletteIO.importPalette(testFile);

        assertEquals(3, palette.size());
        assertTrue(palette.contains(new Color(252, 186, 3)));
        assertTrue(palette.contains(new Color(3, 252, 177)));
        assertTrue(palette.contains(new Color(252, 3, 252)));
    }

    Set<Color> getPalette() {
        Set<Color> colors = new HashSet<>();
        colors.add(new Color(252, 186, 3)); //#fcba03
        colors.add(new Color(3, 252, 177)); //#03fcb1
        colors.add(new Color(252, 3, 252)); //#fc03fc
        return colors;
    }

}