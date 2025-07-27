package xyz.skyjumper409.sendougear;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import xyz.skyjumper409.Main;
import xyz.skyjumper409.sendougear.data.*;
import xyz.skyjumper409.sendougear.data.GearPiece.FullTransform;
import xyz.skyjumper409.sendougear.data.GearPiece.Type;
import xyz.skyjumper409.sendougear.data.GearPiece.VisualState;

public class ImageHandler {
    private BufferedImage img;
    private int w,h;
    public Gear gear;
    private VisualState[] states = new VisualState[3];
    private ImageHandler(BufferedImage inputImage) {
        this.img = inputImage;
        w = img.getWidth();
        h = img.getHeight();
        if(w != 1920 || h != 1080)
            throw new UnsupportedOperationException("Resolutions other than FullHD are not supported yet (resolution received was " + w + "x" + h + ")"); // TODO
        this.gear = new Gear();
    }
    public static ImageHandler calcGear(BufferedImage image) {
        if(image == null) {
            System.err.println("[WARN] image == null");
            return null;
        }
        ImageHandler ih = new ImageHandler(image);
        ih.detectStates();
        ih.detectGear();
        return ih;
    }
    private void detectStates() {
        detectState(Type.HEAD);
        detectState(Type.CLOTHING);
        detectState(Type.SHOES);
    }
    private void detectGear() {
        detectGearPiece(Type.HEAD);
        detectGearPiece(Type.CLOTHING);
        detectGearPiece(Type.SHOES);
    }
    private void detectState(Type type) {
        VisualState closestState = VisualState.RESTING;
        long closestDistance = 1_000_000_000;
        BufferedImage matchImg = VisualState.matchImg;
        int mw = matchImg.getWidth(), mh = matchImg.getHeight();
        for (VisualState state : VisualState.values()) {
            long dist = ImgStuff.thing(img, state.grayDetectCoord, GearPiece.createPiece(type).transform().restingMain.x() -
                gear.head.transform().restingMain.x(), mw, mh, matchImg, null, true);
            if(dist < closestDistance) {
                closestDistance = dist;
                closestState = state;
            }
        }
        this.states[type.idx] = closestState;
        // System.out.println("Determined " + closestState.name + " to be the closest state for GearPiece.Type " + type);
    }
    public double[][] foundDistances = new double[3][4];
    private void detectGearPiece(Type type) {
        GearPiece piece = gear.get(type);
        VisualState state = states[type.idx];
        FullTransform t = piece.applyTransformState(state);
        // // BufferedImage mainImage = img.getSubimage(t.main.x(), t.main.y(), t.mainSize, t.mainSize);
        // // BufferedImage[] subsImages = new BufferedImage[3];
        // // for (int i = 0; i < 3; i++) {
        // //     BiInt bi = t.subs.get(i);
        //     // subsImages[i] = img.getSubimage(bi.x(), bi.y(), t.subSize, t.subSize);
        // // }
        // Ability closestMainAbility = null;
        // long closestMainDist = 1_000_000_000_000_000L;
        // BufferedImage subImg = img.getSubimage(
        //     t.main.x(),
        //     t.main.y(),
        //     t.mainSize, t.mainSize
        // );
        // try {
        //     ImageIO.write(subImg, "PNG", new FileOutputStream("../../../tmp/" + ImgStuff.imgc + ".png"));
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }
        // BufferedImage scaledBufferedImage = null;
        // for (String shortName : Ability.byShort.keySet()) {
        //     Ability ability = Ability.byShort.get(shortName);
        //     if(ability.isMainOnly && ability.exclusiveType != type) continue;
        //     BufferedImage sbi  = scaleBufferedImage(ability.image, ((double)t.mainSize) / ability.image.getWidth());
        //     long dist = ImgStuff.thing(t.main, 0, t.mainSize, t.mainSize, sbi);
        //     if(correctEffects[type.idx][0].shortName.equals(ability.shortName)) {
        //         correctDistances[type.idx][0] = Math.floor(100*Math.sqrt(dist));
        //     }
        //     if(dist < closestMainDist) {
        //         if(Math.abs(Math.sqrt(dist) - Math.sqrt(closestMainDist)) < 10) {
        //             int tmp = ALPHA_THRESHOLD;
        //             ALPHA_THRESHOLD = 128;
        //             long dist2 = thing(t.main, 0, t.mainSize, t.mainSize, sbi);
        //             ALPHA_THRESHOLD = tmp;
        //             if(dist2 >= closestMainDist) continue;
        //         }
        //         closestMainAbility = ability;
        //         closestMainDist = dist;
        //         scaledBufferedImage = sbi;
        //     }
        // }
        MatchedAbility mainMatch = ImgStuff.findClosestAbility(img, t, type, 0, state.abilityBgImg);
        piece.abilities[0] = mainMatch.a();
        foundDistances[type.idx][0] = Math.floor(Main.logMulti * Math.sqrt(mainMatch.dist()));
        // try {
        //     ImageIO.write(scaledBufferedImage, "PNG", new FileOutputStream("../../../tmp/" + (ImgStuff.imgc++) + "_res.png"));
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }
        // // System.out.println("main:");
        // // System.out.println(Ability.byShort.keySet().size());
        // // piece.abilities[0] = closestMainAbility;
        // // System.out.println(piece.abilities[0]);

        // subs
        for (int i = 0; i < 3; i++) {
            // subImg = img.getSubimage(
            //     t.subs.get(i).x(),
            //     t.subs.get(i).y(),
            //     t.subSize, t.subSize
            // );
            // try {
            //     ImageIO.write(subImg, "PNG", new FileOutputStream("../../../tmp/" + ImgStuff.imgc + ".png"));
            // } catch (Exception ex) {
            //     ex.printStackTrace();
            // }
            // Ability closestSubAbility = null;
            // long closestSubDist = 1_000_000_000_000_000L;
            // for (Ability ability : Ability.noMainOnly) {
            //     BufferedImage sbi = scaleBufferedImage(ability.image, ((double)t.subSize) / ability.image.getWidth());
            //     long dist = thing(t.subs.get(i), 0, t.subSize, t.subSize, sbi);
            //     if(correctEffects[type.idx][i+1].shortName.equals(ability.shortName)) {
            //         correctDistances[type.idx][i+1] = Math.floor(100*Math.sqrt(dist));
            //     }
            //     long maybeClosestDist = dist;
            //     int tmp = ALPHA_THRESHOLD;
            //     ALPHA_THRESHOLD = 16;
            //     long dist2 = thing(t.subs.get(i), 0, t.subSize, t.subSize, sbi);
            //     ALPHA_THRESHOLD = tmp;
            //     if(dist >= closestSubDist && dist2 >= closestSubDist) continue;
            //     if(dist < closestSubDist || dist2 < closestSubDist) {
            //         closestSubAbility = ability;
            //         closestSubDist = maybeClosestDist;
            //         scaledBufferedImage = sbi;
            //     }
            // }
            MatchedAbility subMatch = ImgStuff.findClosestAbility(img, t, type, i + 1, state.abilityBgImg);
            piece.abilities[i + 1] = subMatch.a();
            foundDistances[type.idx][i + 1] = Math.floor(Main.logMulti * Math.sqrt(subMatch.dist()));
            // try {
            //     ImageIO.write(scaledBufferedImage, "PNG", new FileOutputStream("../../../tmp/" + (ImgStuff.imgc++) + "_res.png"));
            // } catch (Exception ex) {
            //     ex.printStackTrace();
            // }
        }
    }
    @Override
    public String toString() {
        return Arrays.toString(states) + "\n" + gear;
    }
    public java.util.List<VisualState> getDetectedStates() {
        return java.util.Collections.unmodifiableList(Arrays.asList(states));
    }
}
