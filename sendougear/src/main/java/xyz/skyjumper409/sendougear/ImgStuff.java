package xyz.skyjumper409.sendougear;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import xyz.skyjumper409.Main;
import xyz.skyjumper409.sendougear.data.*;
import xyz.skyjumper409.sendougear.data.GearPiece.FullTransform;
import xyz.skyjumper409.sendougear.data.GearPiece.Type;

public class ImgStuff {
    public static int imgc = 0;
    public static long thing(BufferedImage img, BiInt subImgCoord, int xOffset, int mw, int mh, BufferedImage matchImg, BufferedImage abilityBg) {
        return thing(img, subImgCoord, xOffset, mw, mh, matchImg, abilityBg, false);
    }
    public static long thing(BufferedImage img, BiInt subImgCoord, int xOffset, int mw, int mh, BufferedImage matchImg, BufferedImage abilityBg, boolean ignoreAlpha) {
        BufferedImage subImg = img.getSubimage(
            subImgCoord.x() + xOffset,
            subImgCoord.y(),
            mw, mh
        );
        // try {
        //     ImageIO.write(subImg, "PNG", new FileOutputStream("/home/lynn/Documents/coding/my_projs/senodu_gear/tmp/" + (imgc++) + ".png"));
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }
        long sum = 0, divisor = mw * mh;
        for (int y = 0; y < mh; y++)
            for (int x = 0; x < mw; x++) {
                int dist = pxDiff(matchImg.getRGB(x, y), subImg.getRGB(x, y), ignoreAlpha, ignoreAlpha ? 0 : abilityBg.getRGB(x,y));
                if(dist == Integer.MIN_VALUE)
                    divisor--;
                else
                    sum += dist;
            }
        try {
            long dist = sum / divisor;
            return dist;
        } catch (ArithmeticException aex) {
            System.out.println("wat");
            throw aex;
        }
    }
    private static int clc(double p, int i) {
        // return (int) (i * p + ((0 + i) / 2) * (1 - p));
        return (int) (i * p);
    }
    private static int compositeComponent(int c1, int c2, double a1p, double a2p, double aOut) {
        // chatgpt special
        // (I mean, I understand how this works, soooo)
        double cOut = (c1 * a1p + c2 * a2p * (1 - a1p)) / aOut;
        return (int) cOut;
    }
    private static final int ALPHA_THRESHOLD_COMPOSITE = 2; // any alpha below this value is treated as 0
    private static Color composite(int rgba, int rgbaBg) {
        int a1 = rgba >>> 24;
        int a2 = rgbaBg >>> 24;
        double a1p = a1 / 255.0;
        double a2p = a2 / 255.0;
        int r1 = (rgba >>> 16) & 0xFF, g1 = (rgba >>> 8) & 0xFF, b1 = rgba & 0xFF;
        int r2 = (rgbaBg >>> 16) & 0xFF, g2 = (rgbaBg >>> 8) & 0xFF, b2 = rgbaBg & 0xFF;
        if(a1 < ALPHA_THRESHOLD_COMPOSITE)
            return new Color(clc(a2p, r2), clc(a2p, g2), clc(a2p, b2));
        if(a2 < ALPHA_THRESHOLD_COMPOSITE)
            return new Color(clc(a1p, r1), clc(a1p, g1), clc(a1p, b1));
        double aOut = a1p + a2p * (1 - a1p);
        if(aOut == 0)
            return new Color(0,0,0);
        int rOut = compositeComponent(r1, r2, a1p, a2p, aOut);
        int gOut = compositeComponent(g1, g2, a1p, a2p, aOut);
        int bOut = compositeComponent(b1, b2, a1p, a2p, aOut);
        return new Color(rOut, gOut, bOut);
    }
    private static int ALPHA_THRESHOLD = 32;
    private static int ALPHA_THRESHOLD_B = 128;
    public static int pxDiff(int rgb1, int rgb2, boolean ignoreAlpha, int rgbaBg) {
        // int a1 = rgb1 >>> 24, a2 = rgb2 >>> 24;
        if(!ignoreAlpha)
            // if(a1 < ALPHA_THRESHOLD || a2 < ALPHA_THRESHOLD)
            //     return Integer.MIN_VALUE;
            // else
                // composite ability pixel over ability background pixel
                rgb1 = composite(rgb1, rgbaBg).getRGB();
        int r1 = (rgb1 & 0x00_FF_00_00) >> 16, g1 = (rgb1 & 0x00_00_FF_00) >> 8, b1 = (rgb1 & 0x00_00_00_FF);
        int r2 = (rgb2 & 0x00_FF_00_00) >> 16, g2 = (rgb2 & 0x00_00_FF_00) >> 8, b2 = (rgb2 & 0x00_00_00_FF);
        // if(!ignoreAlpha) {
        //     // double opacPerc = (a1 + 1.0) / 256.0;
        //     // r1 = clc(opacPerc, r1);
        //     // g1 = clc(opacPerc, g1);
        //     // b1 = clc(opacPerc, b1);
        // }
        int rd = r1 - r2, gd = g1 - g2, bd = b1 - b2;
        return rd*rd + gd*gd + bd*bd;
    }
    // https://stackoverflow.com/a/4216635
    public static BufferedImage scaleBufferedImage(BufferedImage img, double scaleFac, int transformOpType) {
        BufferedImage before = img;
        int w = before.getWidth();
        int h = before.getHeight();
        BufferedImage after = new BufferedImage((int)(w * scaleFac), (int)(h * scaleFac), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scaleFac, scaleFac);
        AffineTransformOp scaleOp =
        new AffineTransformOp(at, transformOpType);
        after = scaleOp.filter(before, after);
        return after;
    }
    public static BufferedImage scaleBufferedImage(BufferedImage img, double scaleFac) {
        return scaleBufferedImage(img, scaleFac, AffineTransformOp.TYPE_BILINEAR);
    }
    public static BufferedImage subimage(BufferedImage img, BiInt c, int s) {
        return img.getSubimage(
            c.x(),
            c.y(),
            s, s
        );
    }
    public static void logImage(BufferedImage img, int imgc) {
        logImage(img, imgc+"");
    }
    public static void logImage(BufferedImage img, String filename) {
        if(Main.logImages)
        try {
            ImageIO.write(img, "PNG", new FileOutputStream("/home/lynn/Documents/coding/my_projs/senodu_gear/tmp/" + filename + ".png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static Ability[][] correctEffects = new Ability[3][4];
    public static double[][] correctDistances = new double[3][4];
    static boolean overrideCB = false;
    // private static final boolean ALT_CALC = true;
    public static MatchedAbility findClosestAbility(BufferedImage img, FullTransform t, Type type, int abilityIdx, BufferedImage abilityBg) {
        boolean isMain = abilityIdx == 0;
        BiInt c = isMain ? t.main : t.subs.get(abilityIdx - 1);
        int s = isMain ? t.mainSize : t.subSize;
        BufferedImage subImg = subimage(img, c, s);
        logImage(subImg, imgc);
        BufferedImage scaledBufferedImage = null;
        Ability closestAbility = null;
        long closestDist = 1_000_000_000_000_000L;
        double[] mrow = correctDistances[type.idx];
        Ability[] mrow2 = correctEffects[type.idx];
        for (String shortName : Ability.byShort.keySet()) {
            Ability ability = Ability.byShort.get(shortName);
            if(ability.isMainOnly && (!isMain || ability.exclusiveType != type)) continue;
            BufferedImage sbi  = scaleBufferedImage(ability.image, ((double)s) / ability.image.getWidth());
            int tmpa = ALPHA_THRESHOLD;
            int tmpb = ALPHA_THRESHOLD_B;
            if(overrideCB && ability.shortName.equalsIgnoreCase("CB")) {
                ALPHA_THRESHOLD = 40;
                ALPHA_THRESHOLD_B = 128;
            }
            long dist = thing(img, c, 0, s, s, sbi, abilityBg);
            if(overrideCB && ability.shortName.equalsIgnoreCase("CB")) {
                ALPHA_THRESHOLD = tmpa;
                ALPHA_THRESHOLD_B = tmpb;
            }
            if(mrow2[abilityIdx].shortName.equals(ability.shortName)) {
                mrow[abilityIdx] = Math.floor(Main.logMulti * Math.sqrt(dist));
            }
            if(!isMain) {
                long maybeClosestDist = dist;
                int tmp = ALPHA_THRESHOLD;
                ALPHA_THRESHOLD = 16;
                long dist2 = thing(img, c, 0, s, s, sbi, abilityBg);
                ALPHA_THRESHOLD = tmp;
                if(dist >= closestDist && dist2 >= closestDist) continue;
                if(dist < closestDist || dist2 < closestDist) {
                    closestAbility = ability;
                    closestDist = maybeClosestDist;
                    scaledBufferedImage = sbi;
                }
            } else
            if(dist < closestDist) {
                if(isMain && Math.abs(Math.sqrt(dist) - Math.sqrt(closestDist)) < 10) {
                    int tmp = ALPHA_THRESHOLD;
                    ALPHA_THRESHOLD = 128;
                    long dist2 = thing(img, c, 0, s, s, sbi, abilityBg);
                    ALPHA_THRESHOLD = tmp;
                    if(dist2 >= closestDist) continue;
                }
                closestAbility = ability;
                closestDist = dist;
                scaledBufferedImage = sbi;
            }
        }
        if(Main.logImages)
            logImage(scaledBufferedImage, (imgc++) + "_res");
        return new MatchedAbility(closestAbility, closestDist);
    }
    static {
        String[][] abilities = {"qr,ssu,res,qr".toUpperCase().split(","), "iss,sru,qr,spu".toUpperCase().split(","), "scu,scu,scu,scu".toUpperCase().split(",")};
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 4; j++)
                correctEffects[i][j] = Ability.getByName(abilities[i][j]);
    }
}
