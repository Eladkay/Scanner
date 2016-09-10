package eladkay.scanner.biome;

import eladkay.scanner.ScannerMod;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockBiomeScanner extends Block implements ITileEntityProvider {
    public BlockBiomeScanner() {
        super(Material.IRON);
        setRegistryName(ScannerMod.MODID + ":biomeScanner");
        setUnlocalizedName("biomeScanner");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBiomeScanner();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        ((TileEntityBiomeScanner) worldIn.getTileEntity(pos)).onBlockActivated(playerIn);
        return true;
    }
}
