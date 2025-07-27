package xyz.skyjumper409.sendougear.data;

import xyz.skyjumper409.sendougear.data.GearPiece.Type;

public class Gear {
    public final GearPiece
        head = GearPiece.createPiece(Type.HEAD),
        clothing = GearPiece.createPiece(Type.CLOTHING),
        shoes = GearPiece.createPiece(Type.SHOES);
    private GearPiece[] pieces = new GearPiece[]{head,clothing,shoes};
    public GearPiece get(int idx) {
        return pieces[idx];
    }
    public GearPiece get(Type type) {
        return get(type.idx);
    }
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < pieces.length; i++) {
            s += "\n";
            s += pieces[i];
        }
        return s.substring(1);
    }
    public String toAbilitiesString() {
        String s = "";
        for (int i = 0; i < pieces.length; i++) {
            s += "\n";
            s += pieces[i].toAbilitiesString();
        }
        return s.substring(1);
    }
}
