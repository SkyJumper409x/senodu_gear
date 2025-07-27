package xyz.skyjumper409.sendougear.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.json.JSONObject;

public class I18n {
    private static final HashMap<String, I18n> availableLocales = new HashMap<>();
    public static final I18n en;
    private static I18n currentLocale;
    static {
        try {
            // System.out.println(new File("").getAbsolutePath());
            String[] fns = (Const.langDir.list((parent, fn) -> { return fn.endsWith(".json"); }));
            if(fns.length == 0) {

            }
            for (String fn : fns) {
                String localeName = fn.substring(0, fn.length() - 5);
                I18n i18n = null;
                if(localeName.equalsIgnoreCase("en")) {
                    i18n = new I18n(localeName, fn) {
                        public String getTranslation(String translationKey) {
                            return containsKey(translationKey) ? getTranslationDirect(translationKey) : null;
                        }
                    };
                } else {
                    i18n = new I18n(localeName, fn);
                }
                availableLocales.put(localeName, i18n);
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
            System.exit(2);
        }
        en = availableLocales.get("en");
        currentLocale = en;
    }
    public static void setCurrentLocale(String localeName) {
        I18n i18n = availableLocales.get(localeName);
        if(i18n != null) {
            currentLocale = i18n;
        }
    }
    public static String getTranslation(String translationKey, String localeName) {
        I18n i18n = availableLocales.get(localeName);
        if(i18n == null)
            i18n = en;
        return i18n.getTranslation(translationKey);
    }
    public static String getTranslationForCurrentLocale(String translationKey) {
        return currentLocale.getTranslation(translationKey);
    }

    public final String localeName;
    private final JSONObject translationsObject;
    private final java.util.Set<String> keySet;
    private I18n() throws IOException {
        this(null, null);
    }
    private I18n(String localeName, String localeFilename) throws IOException {
        this.localeName = localeName;
        translationsObject = new JSONObject(new String(
            Files.readAllBytes(Paths.get(
                new File(Const.langDir, localeFilename).toURI()
            )), "UTF-8"
        ));
        keySet = translationsObject.keySet();
        assert(keySet.size() == 26);
    }
    public final boolean containsKey(String translationKey) {
        return keySet.contains(translationKey);
    }
    protected final String getTranslationDirect(String translationKey) {
        // System.out.println("getTranslationDirect(" + translationKey + ")");
        return translationsObject.getString(translationKey);
    }
    public String getTranslation(String translationKey) {
        return keySet.contains(translationKey) ? getTranslationDirect(translationKey) : en.getTranslation(translationKey);
    }
}
