package xyz.skyjumper409.sendougear;

import java.io.File;
import java.io.IOException;
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
    public static JSONObject readJSONObject(File file) throws IOException {
        return new JSONObject(readString(file));
    }
    public static JSONArray readJSONArray(File file) throws IOException {
        return new JSONArray(readString(file));
    }
}
