package image;

import java.awt.*;
import java.util.Set;

public interface PaletteExtractor {
    Set<Color> extract(Color[] pixels);
}
