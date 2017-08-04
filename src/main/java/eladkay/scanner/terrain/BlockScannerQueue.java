package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.librarianlib.common.base.block.ItemModBlock;
import eladkay.scanner.ScannerMod;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static eladkay.scanner.ScannerMod.tab;

public class BlockScannerQueue extends BlockModContainer {
    @org.jetbrains.annotations.Nullable
    @Override
    public ItemBlock createItemForm() {
        return new ItemModBlock(this) {
            @Override
            public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
                tooltip.add("Provides a buffer of chunks and allows you to queue up chunks for scanning.");
                tooltip.add("Place next to Terrain Scanner.");
                super.addInformation(stack, playerIn, tooltip, advanced);
            }
        };
    }

    public BlockScannerQueue() {
        super("scannerQueue", Material.IRON);
        setCreativeTab(tab);
        setHardness(Blocks.IRON_BLOCK.getBlockHardness(null, null, null));
    }

    @Override
    public TileEntity createTileEntity(World worldIn, IBlockState meta) {
        return new TileEntityScannerQueue();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        ScannerMod.proxy.openGuiScannerQueue(((TileEntityScannerQueue) worldIn.getTileEntity(pos)));
        return true;
    }
}
