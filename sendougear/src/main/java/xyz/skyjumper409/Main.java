package xyz.skyjumper409;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
    static File baseDir = new File("/home/lynn/Documents/coding/my_projs/senodu_gear/reference images/");
    static File testDir = new File("/home/lynn/Documents/coding/my_projs/senodu_gear/test builds/");
    public static boolean logImages = true;
    private static final int logDigitsPrecision = 2;
    public static final int logMulti;
    static {
        int lm = 1;
        for (int i = 0; i < logDigitsPrecision; i++)
            lm *= 10;
        logMulti = lm;
    }
    public static void main(String[] args) throws Exception {
        // System.out.println(Ability.getByName("QR").getLocalizedName("de"));
        // GearPiece gp = GearPiece.createPiece(GearPiece.Type.HEAD);
        // System.out.println(gp);
        // gp.applyTransformState(VisualState.HOVER);
        // System.out.println(gp);
        boolean mrow = true;
        ImageHandler ih = null;
        // ih = ImageHandler.calcGear(ImageIO.read(new File(baseDir, "0_weapon_hover.png")));
        // mrow = mrow && checkStates(ih, RESTING, RESTING, RESTING);
        // System.out.println(ih);
        // ih = ImageHandler.calcGear(ImageIO.read(new File(baseDir, "1_headgear_hover.png")));
        // mrow = mrow && checkStates(ih, HOVER, RESTING, RESTING);
        // System.out.println(ih);
        // ih = ImageHandler.calcGear(ImageIO.read(new File(baseDir, "2_clothing_hover.png")));
        // mrow = mrow && checkStates(ih, RESTING, HOVER, RESTING);
        // System.out.println(ih);
        // ih = ImageHandler.calcGear(ImageIO.read(new File(baseDir, "3_shoes_hover.png")));
        // mrow = mrow && checkStates(ih, RESTING, RESTING, HOVER);
        // System.out.println(ih);
        File[] fs = testDir.listFiles();
        if(args.length > 0)
            for (File f : fs)
                testThing(f);
        else
            testThingFull("Screenshot 2025-07-27 02-17-57", RESTING, RESTING, RESTING);
    }
    private static void testThing(File file) throws IOException {
        System.out.println(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1));
        ImageHandler ih = ImageHandler.calcGear(ImageIO.read(file));
        System.out.println(ih.getDetectedStates());
        System.out.println(ih.gear.toAbilitiesString());
    }
    private static void testThingFull(String filename, VisualState... testStates) throws IOException {
        // ImgStuff.correctEffects[0][0] = Ability.getByName("CB");
        File jsonFile = new File(testDir, filename + ".json");
        if(jsonFile.isFile()) {
            JSONArray arr = IOStuff.readJSONArray(jsonFile);
            for (int i = 0; i < 3; i++) {
                JSONObject obj = arr.getJSONObject(i);
                String name = obj.getString("name");
                JSONArray abilities = obj.getJSONArray("abilities");
                int idx = GearPiece.Type.valueOf(name.toUpperCase()).idx;
                for (int j = 0; j < 4; j++) {
                    Ability a = Ability.UNKNOWN;
                    if(i < abilities.length()) {
                        String aName = abilities.getString(j);
                        a = Ability.getByName(aName);
                        if(a == null) {
                            System.err.println("[WARN] Not an ability: \"" + aName + "\"");
                            a = Ability.UNKNOWN;
                        }
                    }
                    ImgStuff.correctEffects[idx][j] = a;
                }
            }
        } else System.err.println("[WARN] missing json file for filename \"" + filename + "\"");
        ImageHandler ih = ImageHandler.calcGear(ImageIO.read(new File(testDir, filename + ".png")));
        boolean statesCheck = checkStates(ih, testStates);
        System.out.println("checkStates: " + statesCheck);
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
    }
    private static boolean checkStates(ImageHandler ih, VisualState... testStates) {
        List<VisualState> states = ih.getDetectedStates();
        for (int i = 0; i < 3; i++)
            if(states.get(i) != testStates[i])
                return false;
        return true;
    }
}
