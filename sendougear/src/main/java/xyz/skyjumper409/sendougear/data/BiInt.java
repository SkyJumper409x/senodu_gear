package xyz.skyjumper409.sendougear.data;

import org.json.JSONArray;

public class BiInt implements Cloneable {
    private int x, y;
    public final boolean isUnmodifiable;
    public BiInt() { this(0,0); }
    public BiInt(JSONArray arr) {
        this(arr.getInt(0), arr.getInt(1));
    }
    public BiInt(int x, int y) {
        this(x, y, false);
    }
    private BiInt(int x, int y, boolean isUnmodifiable) {
        this.x = x;
        this.y = y;
        this.isUnmodifiable = isUnmodifiable;
    }
    public int x() {
        return x;
    }
    public int y() {
        return y;
    }
    public void x(int x) {
        this.x = x;
    }
    public void y(int y) {
        this.y = y;
    }
    public BiInt unmodifiableClone() {
        return new BiInt(x, y, true);
    }
    public BiInt plus(BiInt b) {
        return this.plus(b.x, b.y);
    }
    public BiInt minus(BiInt b) {
        return this.minus(b.x, b.y);
    }
    public BiInt plus(int x, int y) {
        return new BiInt(this.x + x, this.y + y);
    }
    public BiInt minus(int x, int y) {
        return new BiInt(this.x - x, this.y - y);
    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    @Override
    protected BiInt clone() {
        return new BiInt(x, y, isUnmodifiable);
    }
}
