package eladkay.scanner.proxy;

import eladkay.scanner.biome.TileEntityBiomeScanner;
import eladkay.scanner.terrain.TileEntityScannerQueue;
import eladkay.scanner.terrain.TileEntityTerrainScanner;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CommonProxy {
    public void init() {

    }

    public void openGuiBiomeScanner(TileEntityBiomeScanner tileEntity) {

    }

    @Nullable
    public World getWorld() {
        return null;
    }

    public void openGuiTerrainScanner(TileEntityTerrainScanner tileEntity) {

    }

    public void openGuiScannerQueue(TileEntityScannerQueue tileEntity) {

    }
}
