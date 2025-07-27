package xyz.skyjumper409.sendougear.data;

import java.util.List;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import xyz.skyjumper409.sendougear.IOStuff;

public class GearPiece {

    public static class Transform {
        public final BiInt restingMain;
        public final BiInt restingSub;
        public final boolean isBase;
        private Transform baseTransform;
        private Transform(BiInt main, BiInt sub) {
            this(main, sub, true);
        }
        private Transform(BiInt main, BiInt sub, boolean isBase) {
            this.restingMain = main.unmodifiableClone();
            this.restingSub = sub.unmodifiableClone();
            this.baseTransform = this;
            this.isBase = isBase;
        }
        protected Transform(Transform baseTransform) {
            this(baseTransform.restingMain, baseTransform.restingSub, false);
            if(!baseTransform.isBase) throw new IllegalArgumentException("baseTransform.isBase is false");
            this.baseTransform = baseTransform;
        }
        public FullTransform applyState(VisualState state) {
            return new FullTransform(this.baseTransform, state);
        }
        protected Transform clone() {
            return new Transform(this.baseTransform);
        }
        protected final String baseToString() {
            String result = "  " + this.getClass().getName() +
                " {\n    restingMain: " + restingMain +
                ",\n    restingSub: " + restingSub +
                ",\n    isBase: " + isBase +
                ",\n    baseTransform:";
            if(isBase)
                result += " this";
            else
                result += baseTransform.toString().substring(1).replaceAll("\n  ", "\n    ");
            return result + "\n  }";
        }
        @Override
        public String toString() {
            return baseToString();
        }
    }
    public static class FullTransform extends Transform {
        public final BiInt main;
        public final List<BiInt> subs;
        public final int mainSize, subSize;
        public FullTransform(Transform baseTransform, VisualState state) {
            super(baseTransform);
            this.mainSize = state.mainSize;
            this.subSize = state.subSize;

            this.main = this.restingMain.plus(state.mainShift);

            LinkedList<BiInt> subsTmp = new LinkedList<>();
            subsTmp.add(restingSub.plus(state.subShifts.get(0)));
            for (int i = 1; i < state.subShifts.size(); i++)
                subsTmp.add(subsTmp.getLast().plus(state.subShifts.get(i).x() + this.subSize, 0).unmodifiableClone());
            this.subs = Collections.unmodifiableList(subsTmp);
        }
        @Override
        public String toString() {
            String result = super.baseToString();
            result = "  " + this.getClass().getName() + " " + result.substring(result.indexOf("{"), result.length() - 4);
            result += ",\n    main: " + main;
            result += ",\n    subs: " + subs;
            result += ",\n    mainSize: " + mainSize;
            result += ",\n    subSize: " + subSize;
            result += "\n  }";
            return result;
        }
    }
    public static class VisualState {
        public static final VisualState RESTING, HOVER, SELECTED;
        public static final BufferedImage matchImg;
        public static VisualState[] values() {
            return new VisualState[]{RESTING, HOVER, SELECTED};
        }
        public final String name;
        public final BiInt mainShift, rotate;
        public final List<BiInt> subShifts;
        public final int mainSize, subSize;
        public final BiInt grayDetectCoord;
        public final BiInt grayDetectSize;
        private VisualState(String name, BiInt mainShift, List<BiInt> subShifts, BiInt rotate, int mainSize, int subSize, BiInt grayDetectCoord, BiInt grayDetectSize) {
            this.name = name;
            this.mainShift = mainShift.unmodifiableClone();
            this.subShifts = subShifts;
            this.rotate = rotate.unmodifiableClone();
            this.mainSize = mainSize;
            this.subSize = subSize;
            this.grayDetectCoord = grayDetectCoord.unmodifiableClone();
            this.grayDetectSize = grayDetectSize.unmodifiableClone();
        }
        private static VisualState toVisualState(String name, JSONObject transformsObj) {
            JSONObject transformObj = transformsObj.getJSONObject(name);
            JSONObject shiftObj = transformObj.getJSONObject("shift");

            BiInt mainShift = new BiInt(shiftObj.getJSONArray("main"));
            JSONArray subArr = shiftObj.getJSONArray("sub");
            JSONArray subXs = subArr.getJSONArray(0);
            int subY = subArr.getInt(1);
            List<BiInt> subS = new ArrayList<>();
            for (int i = 0; i < subXs.length(); i++)
                subS.add(new BiInt(subXs.getInt(i), subY).unmodifiableClone());

            JSONArray rotateArr = transformObj.getJSONArray("rotate");
            BiInt rotate = new BiInt(rotateArr.getInt(0), rotateArr.getInt(1));

            JSONObject sizeObj = transformObj.getJSONObject("size");

            BiInt grayDetectCoord = new BiInt(transformObj.getJSONArray("grayDetectCoord"));

            BiInt grayDetectSize = new BiInt(transformObj.getJSONArray("grayDetectSize"));
            return new VisualState(name, mainShift, Collections.unmodifiableList(subS), rotate, sizeObj.getInt("main"), sizeObj.getInt("sub"), grayDetectCoord, grayDetectSize);
        }
        static {
            try {
                JSONObject obj = IOStuff.readJSONObject(new java.io.File("gear.json"));
                JSONArray gearArr = obj.getJSONArray("pieces");
                for (int i = 0; i < gearArr.length(); i++) {
                    JSONObject gearObj = gearArr.getJSONObject(i);
                    String name = gearObj.getString("name");
                    JSONObject tfObj = gearObj.getJSONObject("restingCoord");
                    JSONArray mainArr = tfObj.getJSONArray("main");
                    BiInt mainCoord = new BiInt(mainArr.getInt(0), mainArr.getInt(1));
                    JSONArray subArr = tfObj.getJSONArray("sub");
                    BiInt subCoord = new BiInt(subArr.getInt(0), subArr.getInt(1));
                    GearPiece gp = new GearPiece(name, new Transform(mainCoord, subCoord));
                    basePieces[i] = gp;
                    Type t = Type.valueOf(name.toUpperCase());
                    basePiecesMap.put(t, gp);
                }
                JSONObject transformsObj = obj.getJSONObject("stateTransforms");
                RESTING = toVisualState("resting", transformsObj);
                HOVER = toVisualState("hover", transformsObj);
                SELECTED = toVisualState("selected", transformsObj);
                matchImg = ImageIO.read(new java.io.File(Const.imagesDir, "match.png"));
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                System.exit(111);
                throw new RuntimeException(ex.getMessage(), ex.getCause());
            }
        }
        private static void dummy() { }
        @Override
        public String toString() {
            return name;
        }
    }
    static {
        // do these first bc they're needed for VisualState's static initializer
        basePieces = new GearPiece[3];
        basePiecesMap = new HashMap<>();
        // to trigger the static initializer
        VisualState.dummy();
    }
    public static enum Type {
        HEAD(0), CLOTHING(1), SHOES(2);

        public final int idx;
        private Type(int idx) {
            this.idx = idx;
        }
    }
    private static GearPiece[] basePieces;
    private static final HashMap<Type, GearPiece> basePiecesMap;

    public final String name;
    public Ability[] abilities = new Ability[4];
    private VisualState state;
    private Transform transform;
    private GearPiece(String name, Transform transform) {
        this.name = name;
        this.transform = transform;
        applyTransformState(VisualState.RESTING);
    }
    public static GearPiece createPiece(Type type) {
        return basePiecesMap.get(type).makeFromBase();
    }
    private GearPiece makeFromBase() {
        return new GearPiece(name, transform);
    }
    public Transform transform() {
        return transform;
    }
    public FullTransform applyTransformState(VisualState state) {
        if(state == null)
            return null;
        this.state = state;
        FullTransform result = transform.applyState(state);
        transform = result;
        return result;
    }
    @Override
    public String toString() {
        return
            this.getClass().getName() +
            " {\n  name: " + name +
            ",\n  abilities: " + Arrays.toString(abilities) +
            ",\n  state: " + state.name +
            ",\n  transform:" + transform +
            "\n}";
    }
    public String toAbilitiesString() {
        return Arrays.toString(abilities);
    }
}
