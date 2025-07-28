package xyz.skyjumper409;

import java.io.PrintStream;

public class Logger {
    private Logger() { }
    public enum Level {
        DEBUG(false), INFO(false), WARN(true), ERR(true);
        private final boolean isErr;
        private Level(boolean isErr) {
            this.isErr = isErr;
        }
        private PrintStream ps() {
            if(isErr)
                return psErr;
            else
                return psOut;
        }
    }
    private static PrintStream psOut, psErr;
    static {
        try {
            psOut = new PrintStream(new java.io.File("./sendou_gear.log"));
            psErr = psOut;
        } catch (Exception ex) {
            psOut = System.out;
            psErr = System.err;
        }
    }
    public static void log(Level level, Object message) {
        if((level == Level.DEBUG) && !Test.cfg.enableDebugLogging) return;
        level.ps().println("[" + level + "] " + message);
    }
    public static void log(Level level, String message, Object... args) {
        log(level, String.format(message, args));
    }
    public static void debug(Object message) {
        log(Level.DEBUG, message);
    }
    public static void debug(String message, Object... args) {
        log(Level.DEBUG, message, args);
    }
    public static void info(Object message) {
        log(Level.INFO, message);
    }
    public static void info(String message, Object... args) {
        log(Level.INFO, message, args);
    }
    public static void warn(Object message) {
        log(Level.WARN, message);
    }
    public static void warn(String message, Object... args) {
        log(Level.WARN, message, args);
    }
    public static void err(Object message) {
        log(Level.ERR, message);
    }
    public static void err(String message, Object... args) {
        log(Level.ERR, message, args);
    }
}
