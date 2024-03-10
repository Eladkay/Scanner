package eladkay.scanner.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ScannerCreativeTabs extends ItemGroup {
    public ScannerCreativeTabs(String id) {
        super(id);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ModBlocks.TERRAIN_SCANNER.get());
    }
}
