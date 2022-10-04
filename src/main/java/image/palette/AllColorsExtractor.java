package image.palette;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class AllColorsExtractor implements PaletteExtractor{
    @Override
    public Set<Color> extract(Color[] pixels) {
        Set<Color> palette = new HashSet<>();
        for (Color c : pixels)
            palette.add(c);
        return palette;
    }
}
