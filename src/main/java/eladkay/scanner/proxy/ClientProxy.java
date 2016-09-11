package eladkay.scanner.proxy;

import eladkay.scanner.ScannerMod;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        super.init();
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.terrainScanner), 0, new ModelResourceLocation("scanner:terrainScanner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.biomeScannerBasic), 0, new ModelResourceLocation("scanner:biomeScanner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.biomeScannerAdv), 0, new ModelResourceLocation("scanner:biomeScanner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.biomeScannerElite), 0, new ModelResourceLocation("scanner:biomeScanner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.biomeScannerUltimate), 0, new ModelResourceLocation("scanner:biomeScanner", "inventory"));

        /*if(Config.showOutline)
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTerrainScanner.class, new TileEntitySpecialRendererTerrainScanner());*/
    }
}
