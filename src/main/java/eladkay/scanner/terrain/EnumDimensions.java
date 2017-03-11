package eladkay.scanner.terrain;

import net.minecraft.util.IStringSerializable;

public enum EnumDimensions implements IStringSerializable {
    OVERWORLD, NETHER, END, NONE;

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
