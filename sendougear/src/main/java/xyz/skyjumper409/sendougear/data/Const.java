package xyz.skyjumper409.sendougear.data;

import java.io.File;

public class Const {
    private Const() { }
    public static final File
        resourcesDir = new File("./sendougear/target/").exists() ? new File("./sendougear/target/classes") : new File("./"),
        langDir = new File(resourcesDir, "lang/"),
        imagesDir = new File(resourcesDir, "img/"),
        abilityImagesDir = new File(imagesDir, "ability/");
}
