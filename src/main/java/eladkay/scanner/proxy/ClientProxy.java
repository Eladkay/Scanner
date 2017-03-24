package eladkay.scanner.proxy;

import eladkay.scanner.ScannerMod;
import eladkay.scanner.biome.GuiBiomeScanner;
import eladkay.scanner.biome.TileEntityBiomeScanner;
import eladkay.scanner.terrain.GuiScannerQueue;
import eladkay.scanner.terrain.GuiTerrainScanner;
import eladkay.scanner.terrain.TileEntityScannerQueue;
import eladkay.scanner.terrain.TileEntityTerrainScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public class ClientProxy extends CommonProxy {
    private static final String IP = "http://eladkay.pw/scanner/ScannerCallback.php";
    private static boolean sentCallback = false;

    @Override
    public void init() {
        super.init();
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.terrainScanner), 0, new ModelResourceLocation("scanner:terrainScanner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.scannerQueue), 0, new ModelResourceLocation("scanner:scannerQueue", "inventory"));

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.dimensionalCore), 0, new ModelResourceLocation("scanner:dimensionalCore_overworld", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.dimensionalCore), 1, new ModelResourceLocation("scanner:dimensionalCore_nether", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.dimensionalCore), 2, new ModelResourceLocation("scanner:dimensionalCore_end", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.dimensionalCore), 3, new ModelResourceLocation("scanner:dimensionalCore_none", "inventory"));

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.biomeScannerBasic), 0, new ModelResourceLocation("scanner:biomeScanner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.biomeScannerAdv), 0, new ModelResourceLocation("scanner:biomeScanner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.biomeScannerElite), 0, new ModelResourceLocation("scanner:biomeScanner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.biomeScannerUltimate), 0, new ModelResourceLocation("scanner:biomeScanner", "inventory"));

       /* if(Config.showOutline)
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTerrainScanner.class, new TileEntitySpecialRendererTerrainScanner());*/
        MinecraftForge.EVENT_BUS.register(this);

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
