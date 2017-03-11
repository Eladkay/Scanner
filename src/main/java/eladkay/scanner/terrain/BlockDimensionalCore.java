package eladkay.scanner.terrain;

import eladkay.scanner.ScannerMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

import static eladkay.scanner.ScannerMod.tab;

public class BlockDimensionalCore extends Block {
    public BlockDimensionalCore() {
        super(Material.IRON);
        setRegistryName(ScannerMod.MODID + ":dimensionalCore");
        setUnlocalizedName("dimensionalCore");
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
