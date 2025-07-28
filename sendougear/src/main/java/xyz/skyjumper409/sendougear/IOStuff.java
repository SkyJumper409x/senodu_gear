package xyz.skyjumper409.sendougear;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

public class IOStuff {
    private IOStuff() { }
    public static byte[] readAllBytes(File file) throws IOException {
        return Files.readAllBytes(Paths.get(
            file.toURI()
        ));
    }
    public static String readString(File file) throws IOException {
        return new String(readAllBytes(file), "UTF-8");
    }
    public static String readString(InputStream stream) throws IOException {
        return new String(stream.readAllBytes(), "UTF-8");
    }
    public static JSONObject readJSONObject(File file) throws IOException {
        return new JSONObject(readString(file));
    }
    public static JSONArray readJSONArray(File file) throws IOException {
        return new JSONArray(readString(file));
    }
    public static JSONObject readJSONObject(InputStream stream) throws IOException {
        return new JSONObject(readString(stream));
    }
    public static JSONArray readJSONArray(InputStream stream) throws IOException {
        return new JSONArray(readString(stream));
    }
    public static JSONObject readResourceJSONObject(String name) throws IOException {
        return readJSONObject(IOStuff.class.getResourceAsStream(name));
    }
    public static JSONArray readResourceJSONArray(String name) throws IOException {
        return readJSONArray(IOStuff.class.getResourceAsStream(name));
    }
    public static java.awt.image.BufferedImage readResourceImage(String name) throws IOException {
        return javax.imageio.ImageIO.read(IOStuff.class.getResourceAsStream(name));
    }
}
