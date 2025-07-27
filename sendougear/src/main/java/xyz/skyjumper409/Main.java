package xyz.skyjumper409;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import xyz.skyjumper409.sendougear.IOStuff;
import xyz.skyjumper409.sendougear.ImageHandler;
import xyz.skyjumper409.sendougear.ImgStuff;
import xyz.skyjumper409.sendougear.data.*;
import xyz.skyjumper409.sendougear.data.GearPiece.VisualState;
import static xyz.skyjumper409.sendougear.data.GearPiece.VisualState.*;

public class Main {
    static File examplesDir = new File("../../../reference images/");
    static File testDir = new File("../../../test builds/");
    public static boolean logImages = false; // for debugging
    private static final int logDigitsPrecision = 2; // only affects some logged numbers
    public static final int logMulti;
    static {
        int lm = 1;
        for (int i = 0; i < logDigitsPrecision; i++)
            lm *= 10;
        logMulti = lm;
    }
    public static void main(String[] args) throws Exception {
        Ability thermalInk = Ability.getByName("TI");
        for (int i = 0; i < 3; i++) {
            defaultAbilities[i][0] = Ability.UNKNOWN;
            for (int j = 1; j < 4; j++) {
                defaultAbilities[i][j] = thermalInk;
            }
        }
        // System.out.println(Ability.getByName("QR").getLocalizedName("de"));
        // GearPiece gp = GearPiece.createPiece(GearPiece.Type.HEAD);
        // System.out.println(gp);
        // gp.applyTransformState(HOVER);
        // System.out.println(gp);
        // boolean mrow = true;
        // ImageHandler ih = null;
        // ih = ImageHandler.calcGear(ImageIO.read(new File(examplesDir, "0_weapon_hover.png")));
        // mrow = mrow && checkStates(ih, RESTING, RESTING, RESTING);
        // System.out.println(ih);
        // ih = ImageHandler.calcGear(ImageIO.read(new File(examplesDir, "1_headgear_hover.png")));
        // mrow = mrow && checkStates(ih, HOVER, RESTING, RESTING);
        // System.out.println(ih);
        // ih = ImageHandler.calcGear(ImageIO.read(new File(examplesDir, "2_clothing_hover.png")));
        // mrow = mrow && checkStates(ih, RESTING, HOVER, RESTING);
        // System.out.println(ih);
        // ih = ImageHandler.calcGear(ImageIO.read(new File(examplesDir, "3_shoes_hover.png")));
        // mrow = mrow && checkStates(ih, RESTING, RESTING, HOVER);
        // System.out.println(ih);
        // System.out.println(mrow);
        File[] fs = testDir.listFiles((parent, filename) -> filename.endsWith(".png"));
        LinkedList<Boolean> results = new LinkedList<>();
        int correctCount = 0;
        if(args.length == 0) {
            for (File f : fs) {
                boolean result = testThing(f);
                results.add(result);
                if(result) correctCount++;
            }
            System.out.println("results: " + results);
            System.out.println("correct: " + correctCount + "/" + fs.length);
        } else
            testThingFull(args[0], (VisualState[]) null);
    }
    private static final Ability[][] defaultAbilities = new Ability[3][4];
    private static boolean testThing(File file) throws IOException {
        String absPath = file.getAbsolutePath();
        System.out.println(absPath.substring(absPath.lastIndexOf("/") + 1));
        File jsonFile = new File(absPath.substring(0, absPath.lastIndexOf(".")) + ".json");
        VisualState[] testStates = new VisualState[3];
        if(jsonFile.isFile()) {
            abilitiesFromJsonFile(jsonFile, ImgStuff.correctEffects, testStates);
        } else {
            System.err.println("[WARN] missing json file");
            ImgStuff.correctEffects = defaultAbilities;
        }
        ImageHandler ih = ImageHandler.calcGear(ImageIO.read(file));
        System.out.println(ih.getDetectedStates());
        System.out.println(ih.gear.toAbilitiesString());
        boolean states = checkStates(ih, testStates);
        boolean gear = checkGear(ih.gear, ImgStuff.correctEffects);
        return states && gear;
    }
    private static boolean checkGear(Gear gear, Ability[][] testAbilities) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 4; j++)
                if(!gear.get(i).abilities[j].equals(testAbilities[i][j]))
                    return false;
        return true;
    }
    private static boolean testThingFull(String filename, VisualState... testStates) throws IOException {
        // ImgStuff.correctEffects[0][0] = Ability.getByName("CB");
        File jsonFile = new File(testDir, filename + ".json");
        if(jsonFile.isFile()) {
            VisualState[] targetArr = new VisualState[3];
            if(testStates == null)
                testStates = targetArr;
            abilitiesFromJsonFile(jsonFile, ImgStuff.correctEffects, targetArr);
        } else {
            System.err.println("[WARN] missing json file for filename \"" + filename + "\"");
            ImgStuff.correctEffects = defaultAbilities;
        }
        ImageHandler ih = ImageHandler.calcGear(ImageIO.read(new File(testDir, filename + ".png")));
        // System.out.println(ih);
        System.out.println("Dists:");
        System.out.println("found" + (new String(new char[(5 + logDigitsPrecision) * 4 - 5]).replace("\0", " ")) + "\tcorrect");
        for (int i = 0; i < 3; i++)
            System.out.println((Arrays.toString(ih.foundDistances[i]) + "\t" + Arrays.toString(ImgStuff.correctDistances[i])).replaceAll("\\.0",""));
        System.out.println("Gear:");
        System.out.println(ih.gear.toAbilitiesString());
        for (int i = 0; i < 3; i++) {
        String nya = "";
            for (int j = 0; j < 4; j++) {
                nya += Math.floor(ImgStuff.correctDistances[i][j]*10) == Math.floor(ih.foundDistances[i][j]*10) ? "X" : ".";
            }
            System.out.println(nya);
        }
        boolean states = checkStates(ih, testStates);
        boolean gear = checkGear(ih.gear, ImgStuff.correctEffects);
        return states && gear;
    }
    private static TestGear abilitiesFromJsonFile(File jsonFile, Ability[][] outAbilities, VisualState[] outStates) throws IOException {
        TestGear result = null;
        if(outAbilities != null && outStates != null)
            result = new TestGear(outAbilities, outStates);
        else
            result = new TestGear();
        Ability[][] abilities = result.abilities();
        VisualState[] states = result.states();
        JSONArray arr = IOStuff.readJSONArray(jsonFile);
        for (int i = 0; i < 3; i++) {
            JSONObject obj = arr.getJSONObject(i);
            String name = obj.getString("name");
            JSONArray abilitiesArr = obj.getJSONArray("abilities");
            int idx = GearPiece.Type.valueOf(name.toUpperCase()).idx;
            for (int j = 0; j < 4; j++) {
                Ability a = Ability.UNKNOWN;
                if(i < abilitiesArr.length()) {
                    String aName = abilitiesArr.getString(j);
                    a = Ability.getByName(aName);
                    if(a == null) {
                        System.err.println("[WARN] Not an ability: \"" + aName + "\"");
                        a = Ability.UNKNOWN;
                    }
                }
                abilities[idx][j] = a;
            }
            states[i] = VisualState.valueOf(obj.getString("state"));
        }
        return result;
    }
    private static boolean checkStates(ImageHandler ih, VisualState... testStates) {
        List<VisualState> states = ih.getDetectedStates();
        for (int i = 0; i < 3; i++)
            if(states.get(i) != testStates[i])
                return false;
        return true;
    }
}
