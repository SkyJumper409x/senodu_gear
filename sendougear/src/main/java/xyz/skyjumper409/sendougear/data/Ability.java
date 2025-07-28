package xyz.skyjumper409.sendougear.data;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import xyz.skyjumper409.sendougear.IOStuff;

public class Ability {
    public static final Map<String, Ability> byShort, byInternal;
    public static final Set<Ability> mainOnly, noMainOnly, values;
    public static final Ability UNKNOWN;
    public static final Ability NULL_ABILITY;
    static {
        try {
            HashMap<String, Ability> aShort = new HashMap<>(), aInternal = new HashMap<>();
            HashSet<Ability> aMain = new HashSet<>(), aSub = new HashSet<>(), vals = new HashSet<>();
            JSONArray abilitiesJSON = IOStuff.readResourceJSONArray("/abilities.json");
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
                vals.add(a);
            }
            byShort = Collections.unmodifiableMap(aShort);
            byInternal = Collections.unmodifiableMap(aInternal);
            mainOnly = Collections.unmodifiableSet(aMain);
            noMainOnly = Collections.unmodifiableSet(aSub);
            values = Collections.unmodifiableSet(vals);
            UNKNOWN = getByName("UNKNOWN");
            NULL_ABILITY = new Ability(false, null, null, "NULL_ABILITY");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            System.exit(111);
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    public static Set<String> keySetShort() {
        return byShort.keySet();
    }
    public static Set<String> keySetInternal() {
        return byInternal.keySet();
    }
    public static Ability getByName(String name) {
        Ability a = byShort.get(name);
        if(a != null) return a;
        a = byInternal.get(name);
        if(a == null)
            return Ability.NULL_ABILITY;
        return a;
    }

    public final boolean isMainOnly;
    public final GearPiece.Type exclusiveType;
    public final String shortName, translationKey, internalName;
    public final BufferedImage image;
    Ability(boolean isMainOnly, GearPiece.Type exclusiveType, String shortName, String internalName) throws IOException {
        this.isMainOnly = isMainOnly;
        this.exclusiveType = exclusiveType;
        if(shortName == null) {
            this.shortName = "NULL";
            this.image = UNKNOWN.image;
        } else {
            this.shortName = shortName.toUpperCase();
            this.image = IOStuff.readResourceImage("/img/ability/" + this.shortName + ".png");
        }
        this.translationKey = "ABILITY_" + this.shortName;
        this.internalName = internalName;
    }
    public String getLocalizedName(String locale) {
        return I18n.getTranslation(translationKey, locale);
    }
    @Override
    public String toString() {
        return shortName;
    }
}
