package xyz.skyjumper409.sendougear.data;

public record TestGear(Ability[][] abilities, GearPiece.VisualState[] states) {
    public TestGear() {
        this(new Ability[3][4], new GearPiece.VisualState[3]);
    }
}
