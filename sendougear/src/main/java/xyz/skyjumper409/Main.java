package xyz.skyjumper409;

import java.io.File;
import java.io.IOException;

import xyz.skyjumper409.sendougear.ImageHandler;

public class Main {
    public static final Config cfg = new Config();
    public static void main(String[] args) {
        if(args.length < 1) {
            // System.err.println("one or more arguments are required");
            App.main(args);
        }
        if(args[0] == "--links-only")
            cfg.setGearLogMode(Config.GEAR_LOG_MODE_LINK);
        if(args[0] == "--no-links")
            cfg.setGearLogMode(Config.GEAR_LOG_MODE_EFFS);
        if(args[0] == "--no-logging")
            cfg.setGearLogMode(0);
        for(String filepath : args) {
            File file = new File(filepath);
            if(file.isFile())
                try {
                    System.out.println(ImageHandler.calcGear(file).gear.toAbilitiesString());
                } catch (IOException ioex) {
                    System.err.println("Exception occured for filepath \"" + filepath + "\":");
                    ioex.printStackTrace(System.err);
                }
            else
                System.err.println("not a file: \"" + filepath + "\"");
        }
    }
}
