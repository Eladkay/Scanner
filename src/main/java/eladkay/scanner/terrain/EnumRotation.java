package eladkay.scanner.terrain;

public enum EnumRotation {
    POSX_POSZ(1, 1),
    POSX_NEGZ(1, 0),
    NEGX_POSZ(0, 1),
    NEGX_NEGZ(0, 0);

    EnumRotation(int x, int z) {
        this.x = x;
        this.z = z;
    }

    int x;
    int z;

    public EnumRotation getNext() {
        return values()[(ordinal() + 1) % values().length];
    }
}
