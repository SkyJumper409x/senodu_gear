package xyz.skyjumper409.sendougear.data;

import xyz.skyjumper409.Main;
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
        if(!Main.cfg.doLogGear()) return this.getClass().getName();
        StringBuilder sb = new StringBuilder("");
        if(Main.cfg.doLogGearEffs())
            for (int i = 0; i < pieces.length; i++)
                sb = sb.append(pieces[i].toAbilitiesString()).append("\n");
        if(Main.cfg.doLogGearLink()) {
            sb.append(toURLString());
        } else sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    public String toURLString() {
        StringBuilder sb = new StringBuilder("https://sendou.ink/analyzer?weapon=0&build=");
        for (int i = 0; i < pieces.length; i++) {
            Ability[] abilities = pieces[i].abilities;
            for (int j = 0; j < abilities.length; j++) {
                // %2C is just a comma, but sendou.ink urlencodes commas for some reason
                String name = abilities[j].shortName;
                if(abilities[j] == Ability.UNKNOWN) name = "U";
                sb = sb.append(name).append("%2C");
            }
        }
        sb = sb.delete(sb.length() - 3, sb.length());
        sb = sb.append("&build2=U%2CU%2CU%2CU%2CU%2CU%2CU%2CU%2CU%2CU%2CU%2CU&lde=0&focused=1");
        return sb.toString();
    }
}
