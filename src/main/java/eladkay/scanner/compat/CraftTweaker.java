package eladkay.scanner.compat;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class CraftTweaker {
    public static void init() {
        if (!Loader.isModLoaded("CraftTweaker")) return;
        CraftTweakerAPI.registerClass(ScannerCT.class);
    }

    @Nonnull
    public static ItemStack[] getStacks(IItemStack[] stacks) {
        ItemStack[] ret = new ItemStack[stacks.length];
        for (int i = 0; i < stacks.length; i++)
            ret[i] = CraftTweakerMC.getItemStack(stacks[i]);
        return ret;
    }

}
