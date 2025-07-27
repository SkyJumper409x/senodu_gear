package xyz.skyjumper409.sendougear.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import xyz.skyjumper409.sendougear.IOStuff;

public class Ability {
    public static final Map<String, Ability> byShort, byInternal;
    public static final Set<Ability> mainOnly, noMainOnly;
    public static final Ability UNKNOWN;
    static {
        try {
            HashMap<String, Ability> aShort = new HashMap<>(), aInternal = new HashMap<>();
            HashSet<Ability> aMain = new HashSet<>(), aSub = new HashSet<>();
            JSONArray abilitiesJSON = IOStuff.readJSONArray(new File(Const.resourcesDir, "abilities.json"));
            for (int i = 0; i < abilitiesJSON.length(); i++) {
                JSONObject obj = abilitiesJSON.getJSONObject(i);
                String shortName = obj.getString("short");
                String internalName = obj.getString("internal");
                boolean isMainOnly = obj.getBoolean("mainonly");
                GearPiece.Type exclType = null;
                if(isMainOnly)
                    exclType = GearPiece.Type.valueOf(obj.getString("exclusivePiece").toUpperCase());
                Ability a = new Ability(isMainOnly, exclType, shortName, internalName);
                aShort.put(shortName, a);
                aInternal.put(internalName, a);
                if(isMainOnly)
                    aMain.add(a);
                else
                    aSub.add(a);
            }
            byShort = Collections.unmodifiableMap(aShort);
            byInternal = Collections.unmodifiableMap(aInternal);
            mainOnly = Collections.unmodifiableSet(aMain);
            noMainOnly = Collections.unmodifiableSet(aSub);
            UNKNOWN = getByName("UNKNOWN");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            System.exit(111);
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    public static Ability getByName(String name) {
        Ability a = byShort.get(name);
        if(a != null) return a;
        return byInternal.get(name);
    }

    public final boolean isMainOnly;
    public final GearPiece.Type exclusiveType;
    public final String shortName, translationKey, internalName;
    public final BufferedImage image;
    Ability(boolean isMainOnly, GearPiece.Type exclusiveType, String shortName, String internalName) throws IOException {
        this.isMainOnly = isMainOnly;
        this.exclusiveType = exclusiveType;
        this.shortName = shortName.toUpperCase();
        this.translationKey = "ABILITY_" + this.shortName;
        this.internalName = internalName;
        image = ImageIO.read(new File(Const.abilityImagesDir, this.shortName + ".png"));
    }
    public String getLocalizedName(String locale) {
        return I18n.getTranslation(translationKey, locale);
    }
    @Override
    public String toString() {
        return shortName;
    }
}
