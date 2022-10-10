package tracing.conversions;


import geometry.Path;

import java.awt.Color;
import java.util.List;

public record ColorPaths(Color color, List<Path> paths) {}
