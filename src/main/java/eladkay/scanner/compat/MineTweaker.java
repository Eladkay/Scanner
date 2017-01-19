package eladkay.scanner.compat;

import net.minecraftforge.fml.common.Loader;

public class MineTweaker {
    public static void init() {
        if (!Loader.isModLoaded("MineTweaker3")) return;
        //MineTweakerAPI.registerClass(ScannerMT.class);
    }

//    @Nonnull
//    public static ItemStack[] getStacks(IItemStack[] stacks) {
//        ItemStack[] ret = new ItemStack[stacks.length];
//        for (int i = 0; i < stacks.length; i++)
//            ret[i] = MineTweakerMC.getItemStack(stacks[i]);
//        return ret;
//    }

}
