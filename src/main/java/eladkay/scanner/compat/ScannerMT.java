package eladkay.scanner.compat;

//@ZenClass("mods.scanner.Scanner")
public class ScannerMT {
//    @ZenMethod
//    public static void addRecipe(IItemStack stack, int rarity, int minY, int maxY) {
//        MineTweakerAPI.apply(new Add(MineTweakerMC.getItemStack(stack), rarity, minY, maxY));
//    }
//
//    public static class Add implements IUndoableAction {
//        ItemStack stack;
//        int rarity;
//        int maxY;
//        int minY;
//
//        public Add(ItemStack stack, int rarity, int minY, int maxY) {
//            this.stack = stack;
//            this.rarity = rarity;
//            this.minY = minY;
//            this.maxY = maxY;
//        }
//
//        @Override
//        public void apply() {
//            if (!(stack.getItem() instanceof ItemBlock)) return;
//            Block block = ((ItemBlock) stack.getItem()).getBlock();
//            IBlockState blockState = block.getStateFromMeta(stack.getItemDamage());
//            Oregistry.registerEntry(new Oregistry.Entry(blockState, rarity, maxY, minY));
//        }
//
//        @Override
//        public boolean canUndo() {
//            return false;
//        }
//
//        @Override
//        public void undo() {
//
//        }
//
//        @Override
//        public String describe() {
//            return "Add an ore entry.";
//        }
//
//        @Override
//        public String describeUndo() {
//            return "Remove an ore entry.";
//        }
//
//        @Override
//        public Object getOverrideKey() {
//            return null;
//        }
//    }
}
