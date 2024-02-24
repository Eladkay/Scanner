package eladkay.scanner.misc;

public enum EnumRotation {
    POSX_POSZ(1, 1),
    POSX_NEGZ(1, -1),
    NEGX_POSZ(-1, 1),
    NEGX_NEGZ(-1, -1);

    EnumRotation(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public final int x;
    public final int z;

    public EnumRotation getNext() {
        return values()[(ordinal() + 1) % values().length];
    }
}
