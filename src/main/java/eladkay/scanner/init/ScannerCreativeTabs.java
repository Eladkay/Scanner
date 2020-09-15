package eladkay.scanner.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import static eladkay.scanner.init.ModBlocks.terrainScanner;

public class ScannerCreativeTabs extends CreativeTabs {
    public ScannerCreativeTabs(String id) {
        super(id);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(Item.getItemFromBlock(terrainScanner));
    }
}
