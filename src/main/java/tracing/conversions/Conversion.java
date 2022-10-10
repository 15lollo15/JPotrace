package tracing.conversions;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public interface Conversion {
    String convert(BufferedImage img, int scale);
    String convert(BufferedImage img);
    void setStatusCallback(Consumer<String> callback);
}
