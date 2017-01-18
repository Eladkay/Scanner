package eladkay.scanner.terrain;

import eladkay.scanner.ScannerMod;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static eladkay.scanner.ScannerMod.tab;

public class BlockScannerQueue extends Block implements ITileEntityProvider {
    public BlockScannerQueue() {
        super(Material.IRON);
        setRegistryName(ScannerMod.MODID + ":scannerQueue");
        setUnlocalizedName("scannerQueue");
        setCreativeTab(tab);
        setHardness(Blocks.IRON_BLOCK.getBlockHardness(null, null, null));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityScannerQueue();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        ScannerMod.proxy.openGuiScannerQueue(((TileEntityScannerQueue) worldIn.getTileEntity(pos)));
        return true;
    }
}
