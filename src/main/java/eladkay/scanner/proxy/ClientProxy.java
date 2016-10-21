package eladkay.scanner.proxy;

import com.feed_the_beast.ftbl.lib.gui.GuiLM;
import eladkay.scanner.ScannerMod;
import eladkay.scanner.biome.GuiBiomeScanner;
import eladkay.scanner.biome.TileEntityBiomeScanner;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
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

       /* if(Config.showOutline)
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTerrainScanner.class, new TileEntitySpecialRendererTerrainScanner());*/
    }

    @Override
    public void openGuiBiomeScanner(TileEntityBiomeScanner tileEntity) {
        new GuiBiomeScanner(tileEntity).openGui();
    }
}
