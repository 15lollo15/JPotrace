package image;

import java.awt.*;
import java.io.File;
import java.util.Set;

public interface PaletteIO {

    Set<Color> importPalette(File f);

    void exportPalette(PaletteType paletteType, Set<Color> palette, File f);

    enum PaletteType {
        RGB,
        HEX
    }
}
