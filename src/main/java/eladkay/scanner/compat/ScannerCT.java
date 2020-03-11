package eladkay.scanner.compat;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.scanner.Scanner")
@ZenRegister
public class ScannerCT {
    @ZenMethod
    public static void addRecipe(IItemStack stack, IItemStack materialStack, int rarity, int minY, int maxY) {
        CraftTweakerAPI.apply(new Add(CraftTweakerMC.getItemStack(stack), CraftTweakerMC.getItemStack(materialStack), rarity, minY, maxY));
    }

    public static class Add implements IAction {
        private ItemStack stack;
        private ItemStack materialStack;
        private int rarity;
        private int maxY;
        private int minY;

        public Add(ItemStack stack, ItemStack materialStack, int rarity, int minY, int maxY) {
            this.stack = stack;
            this.materialStack = materialStack;
            this.rarity = rarity;
            this.minY = minY;
            this.maxY = maxY;
        }

        @Override
        public void apply() {
            if (!(stack.getItem() instanceof ItemBlock)) return;
            if (!(materialStack.getItem() instanceof ItemBlock)) return;
            Block block = ((ItemBlock) stack.getItem()).getBlock();
            IBlockState blockState = block.getStateFromMeta(stack.getItemDamage());
            Block materialBlock = ((ItemBlock) materialStack.getItem()).getBlock();
            IBlockState materialState = materialBlock.getStateFromMeta(materialStack.getItemDamage());
            Oregistry.registerEntry(new Oregistry.Entry(blockState, materialState, rarity, maxY, minY));
        }

        @Override
        public String describe() {
            return "Add an ore entry.";
        }
    }
}
