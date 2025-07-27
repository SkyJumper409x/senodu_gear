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
            s += pieces[i].toAbilitiesString();
            s += "\n";
        }
        s += "https://sendou.ink/analyzer?weapon=0&build=";
        for (int i = 0; i < pieces.length; i++) {
            Ability[] abilities = pieces[i].abilities;
            for (int j = 0; j < abilities.length; j++) {
                s += abilities[j].shortName;
                s += "%2C"; // just commas, but sendou.ink urlencodes em for some reason
            }
        }
        if(s.contains("UNKNOWN"))
            s = s.replaceAll("UNKNOWN", "U");
        s = s.substring(0, s.length() - 3) + "&build2=U,U,U,U,U,U,U,U,U,U,U,U&lde=0&focused=1".replaceAll(",", "%2C");
        return s;
    }
}
