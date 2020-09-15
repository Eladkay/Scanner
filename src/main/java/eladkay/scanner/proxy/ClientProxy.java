package eladkay.scanner.proxy;

import eladkay.scanner.biome.GuiBiomeScanner;
import eladkay.scanner.biome.TileEntityBiomeScanner;
import eladkay.scanner.terrain.GuiScannerQueue;
import eladkay.scanner.terrain.GuiTerrainScanner;
import eladkay.scanner.terrain.TileEntityScannerQueue;
import eladkay.scanner.terrain.TileEntityTerrainScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
    }


    @Override
    public void openGuiBiomeScanner(TileEntityBiomeScanner tileEntity) {
        new GuiBiomeScanner(tileEntity).openGui();
    }

    @Override
    public void openGuiTerrainScanner(TileEntityTerrainScanner tileEntity) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiTerrainScanner(tileEntity));
    }

    @Override
    @Nullable
    public World getWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public void openGuiScannerQueue(TileEntityScannerQueue tileEntity) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiScannerQueue(tileEntity));
    }
}
