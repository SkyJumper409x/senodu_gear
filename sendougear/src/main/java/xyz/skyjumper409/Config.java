package xyz.skyjumper409;

public class Config {
    public static final int
    GEAR_LOG_MODE_NONE = 0b00,
    GEAR_LOG_MODE_LINK = 0b01,
    GEAR_LOG_MODE_EFFS = 0b10,
    GEAR_LOG_MODE_BOTH = 0b11;

    boolean enableImageLogging = false; // for debugging
    int logDigitsPrecision = 2; // only affects some logged numbers
    int logMulti;
    boolean enableDebugLogging = true;
    int gearLogMode = GEAR_LOG_MODE_BOTH;
    void setEnableImageLogging(boolean logImages) {
        this.enableImageLogging = logImages;
    }
    void setLogDigitsPrecision(int logDigitsPrecision) {
        this.logDigitsPrecision = logDigitsPrecision;
        int lm = 1;
        for (int i = 0; i < logDigitsPrecision; i++)
            lm *= 10;
        this.logMulti = lm;
    }
    void setEnableDebugLogging(boolean enableDebugLogging) {
        this.enableDebugLogging = enableDebugLogging;
    }
    public void setGearLogMode(int gearLogMode) {
        if(0 <= gearLogMode && gearLogMode <= 0b11)
            this.gearLogMode = gearLogMode;
    }
    public boolean isImageLoggingEnabled() {
        return enableImageLogging;
    }
    public int logDigitsPrecision() {
        return logDigitsPrecision;
    }
    public int logMulti() {
        return logMulti;
    }
    public boolean isDebugLoggingEnabled() {
        return enableDebugLogging;
    }
    public boolean doLogGear() {
        return gearLogMode > 0;
    }
    public boolean doLogGearEffs() {
        return (gearLogMode & GEAR_LOG_MODE_EFFS) == GEAR_LOG_MODE_EFFS;
    }
    public boolean doLogGearLink() {
        return (gearLogMode & GEAR_LOG_MODE_LINK) == GEAR_LOG_MODE_LINK;
    }
}
