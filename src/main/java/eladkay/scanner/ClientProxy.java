package eladkay.scanner;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        super.init();
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.scanner), 0, new ModelResourceLocation("scanner:scanner", "inventory"));
    }
}
