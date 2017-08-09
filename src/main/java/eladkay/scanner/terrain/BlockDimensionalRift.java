package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.librarianlib.common.base.block.ItemModBlock;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import eladkay.scanner.ScannerMod;
import eladkay.scanner.misc.TileEnergyConsumer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static eladkay.scanner.ScannerMod.tab;

public class BlockDimensionalRift extends BlockModContainer {

    @Nullable
    @Override
    public ItemBlock createItemForm() {
        return new ItemModBlock(this) {
            @Override
            public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
                if (stack.getItemDamage() == EnumDimensions.NONE.ordinal()) tooltip.add("Crafting component.");
                else {
                    tooltip.add("Allows you to create Dimensional Core blocks.");
                    tooltip.add("Place in the End, Nether, or Overworld, to get the respective");
                    tooltip.add("dimensional core.");
                }
                super.addInformation(stack, playerIn, tooltip, advanced);
            }
        };
    }

    public BlockDimensionalRift() {
        super("dimensionalRift", Material.IRON);
        setCreativeTab(tab);
        setHardness(Blocks.IRON_BLOCK.getBlockHardness(null, null, null));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState iBlockState) {
        return new TileDimensionalRift();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @TileRegister("rift")
    public static class TileDimensionalRift extends TileEnergyConsumer implements ITickable {

//        @Override
//        public boolean hasFastRenderer() {
//            return true;
//        }

        public static final int TICKS_TO_COMPLETION = 20 * 10/*60 * 10*/;
        public static final int CAPACITY = 1_000_000;

        public TileDimensionalRift() {
            super(CAPACITY);
        }

        public @Save
        int ticks = 0;

        /**
         * Like the old updateEntity(), except more generic.
         */
        @Override
        public void update() {
            ticks++;
            if (ticks >= TICKS_TO_COMPLETION) {
                transform();
            }
        }

        private void transform() {
            BlockDimensionalCore dimensionalCore = ScannerMod.dimensionalCore;
            PropertyEnum<EnumDimensions> type = BlockDimensionalCore.TYPE;
            switch (getWorld().provider.getDimensionType()) {
                case NETHER:
                    getWorld().setBlockState(getPos(), dimensionalCore.getDefaultState().withProperty(type, EnumDimensions.NETHER));
                    break;
                case THE_END:
                    getWorld().setBlockState(getPos(), dimensionalCore.getDefaultState().withProperty(type, EnumDimensions.END));
                    break;
                case OVERWORLD:
                default:
                    getWorld().setBlockState(getPos(), dimensionalCore.getDefaultState().withProperty(type, EnumDimensions.OVERWORLD));
                    break;
            }
        }
    }
}
