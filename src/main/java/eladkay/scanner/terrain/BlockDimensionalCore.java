package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.librarianlib.common.base.block.ItemModBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static eladkay.scanner.ScannerMod.tab;

public class BlockDimensionalCore extends BlockMod {
    @Nullable
    @Override
    public ItemBlock createItemForm() {
        return new ItemModBlock(this) {
            @Override
            public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
                if (stack.getItemDamage() == EnumDimensions.NONE.ordinal()) tooltip.add("Crafting component.");
                else {
                    tooltip.add("Allows you to build chunks from other dimensions. ");
                    tooltip.add("Place next to Terrain Scanner.");
                }
                super.addInformation(stack, playerIn, tooltip, advanced);
            }

            @Override
            public String getUnlocalizedName(ItemStack stack) {
                return super.getUnlocalizedName(stack) + "_" + EnumDimensions.values()[stack.getMetadata()].getName();
            }

            @Override
            public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
                IBlockState iblockstate = worldIn.getBlockState(pos);
                Block block = iblockstate.getBlock();

                if (!block.isReplaceable(worldIn, pos)) {
                    pos = pos.offset(facing);
                }

                if (stack.stackSize != 0 && playerIn.canPlayerEdit(pos, facing, stack) && worldIn.canBlockBePlaced(this.block, pos, false, facing, null, stack)) {
                    int i = stack.getMetadata();
                    IBlockState iblockstate1 = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, playerIn, stack);

                    if (placeBlockAt(stack, playerIn, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1)) {
                        SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, playerIn);
                        worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        --stack.stackSize;
                    }

                    return EnumActionResult.SUCCESS;
                } else {
                    return EnumActionResult.FAIL;
                }
            }
        };
    }

    public BlockDimensionalCore() {
        super("dimensionalCore", Material.IRON);
        setCreativeTab(tab);
        setHardness(Blocks.IRON_BLOCK.getBlockHardness(null, null, null));

    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < TYPE.getAllowedValues().size(); i++) list.add(new ItemStack(itemIn, 1, i));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, EnumDimensions.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    public static final PropertyEnum<EnumDimensions> TYPE = PropertyEnum.create("type", EnumDimensions.class);
}
