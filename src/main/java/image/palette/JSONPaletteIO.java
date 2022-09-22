package image.palette;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class JSONPaletteIO implements PaletteIO{

    @Override
    public Set<Color> importPalette(File f) {
        Set<Color> palette = new HashSet<>();
        try (FileReader fr = new FileReader(f)) {
            JSONTokener tokenizer = new JSONTokener(fr);
            JSONObject json = new JSONObject(tokenizer);
            PaletteType paletteType = PaletteType.valueOf(json.getString("type"));
            JSONArray paletteArray = json.getJSONArray("palette");
            paletteArray.forEach(o -> {
                JSONObject colorObject = (JSONObject) o;
                if (paletteType == PaletteType.HEX) {
                    String hex = colorObject.getString("h");
                    palette.add(Color.decode(hex));
                } else if (paletteType == PaletteType.RGB) {
                    int r = colorObject.getInt("r");
                    int g = colorObject.getInt("g");
                    int b = colorObject.getInt("b");
                    palette.add(new Color(r, g, b));
                }else{
                    throw new UnknownPaletteType();
                }
            });

        } catch (IOException e) {
            throw new FileCreationException();
        }

        return palette;
    }

    @Override
    public void exportPalette(PaletteType paletteType, Set<Color> palette, File f) {
        JSONObject json = new JSONObject();
        json.put("type", paletteType);

        JSONArray paletteJsonArray = new JSONArray();
        for (Color c : palette) {
            JSONObject colorObject = new JSONObject();
            int r = c.getRed();
            int g = c.getGreen();
            int b = c.getBlue();
            if (paletteType == PaletteType.HEX) {
                String hex = String.format("#%02x%02x%02x", r, g, b);
                colorObject.put("h", hex);
            }else if (paletteType == PaletteType.RGB){
                colorObject.put("r", r);
                colorObject.put("g", g);
                colorObject.put("b", b);
            }
            paletteJsonArray.put(colorObject);
        }
        json.put("palette", paletteJsonArray);

        try (FileWriter fw = new FileWriter(f)) {
            fw.write(json.toString());
        } catch (IOException e) {
            throw new FileCreationException();
        }
    }

    public static class UnknownPaletteType extends RuntimeException{}

    public static class FileCreationException extends RuntimeException{}
}
