package xyz.skyjumper409;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import xyz.skyjumper409.sendougear.ImageHandler;

public class Main {
    public static final Config cfg = new Config();
    public static void main(String[] args) {
        if(args.length < 1) {
            // System.err.println("one or more arguments are required");
            App.main(args);
            return;
        }
        if(args[0] != null && args[0].startsWith("--gear-log-mode=")) {
            args[0] = args[0].substring("--gear-log-mode=".length());
            if(argInArgs(args[0], "link-only", "no-abilities"))
                cfg.setGearLogMode(Config.GEAR_LOG_MODE_LINK);
            else if(argInArgs(args[0], "abilities-only", "no-link"))
                cfg.setGearLogMode(Config.GEAR_LOG_MODE_EFFS);
            else if(args[0].equals("no-logging"))
                cfg.setGearLogMode(0);
            else {
                System.err.println("invalid value for --gear-log-mode");
                System.exit(1);
            }
            args = Arrays.copyOfRange(args, 1, args.length);
        }
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
    private static boolean argInArgs(String arg, String... args) {
        for (int i = 0; i < args.length; i++)
            if(args[i].equals(arg)) return true;
        return false;
    }
}
