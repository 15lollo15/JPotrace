package image.palette;

import java.awt.*;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class AllColorsExtractor implements PaletteExtractor{
    @Override
    public Set<Color> extract(Color[] pixels) {
        return Arrays.stream(pixels).collect(Collectors.toSet());
    }
}
