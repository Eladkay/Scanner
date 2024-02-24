package eladkay.scanner.compat;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.actions.IAction;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.openzen.zencode.java.ZenCodeType;

@ZenCodeType.Name("mods.scanner.Scanner")
@ZenRegister
public class ScannerCT {
    @ZenCodeType.Method
    public static void addOre(ItemStack stack, ItemStack materialStack, int rarity, int minY, int maxY) {
        CraftTweakerAPI.apply(new Add(stack, materialStack, rarity, minY, maxY));
    }

    public static class Add implements IAction {
        private final ItemStack stack;
        private final ItemStack materialStack;
        private final int rarity;
        private final int maxY;
        private final int minY;

        public Add(ItemStack stack, ItemStack materialStack, int rarity, int minY, int maxY) {
            this.stack = stack;
            this.materialStack = materialStack;
            this.rarity = rarity;
            this.minY = minY;
            this.maxY = maxY;
        }

        @Override
        public void apply() {
            if (!(stack.getItem() instanceof BlockItem)) return;
            if (!(materialStack.getItem() instanceof BlockItem)) return;
            Block block = ((BlockItem) stack.getItem()).getBlock();
            BlockState blockState = block.defaultBlockState();
            Block materialBlock = ((BlockItem) materialStack.getItem()).getBlock();
            BlockState materialState = materialBlock.defaultBlockState();
            Oregistry.registerEntry(new Oregistry.Entry(blockState, materialState, rarity, maxY, minY));
        }

        @Override
        public String describe() {
            return "Add an ore entry.";
        }
    }
}
