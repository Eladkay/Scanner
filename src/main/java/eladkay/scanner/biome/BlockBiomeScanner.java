package eladkay.scanner.biome;

import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.librarianlib.common.base.block.ItemModBlock;
import net.minecraft.block.ITileEntityProvider;
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

public class BlockBiomeScanner extends BlockMod implements ITileEntityProvider {
    int type;

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemBlock createItemForm() {
        return new ItemModBlock(this) {
            @Override
            public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
                tooltip.add("Provides info about biomes far away.");
                super.addInformation(stack, playerIn, tooltip, advanced);
            }
        };
    }

    public BlockBiomeScanner(int i) {
        super(i == 0 ? "biomeScannerBasic" : i == 1 ? "biomeScannerAdv" : i == 2 ? "biomeScannerElite" : i == 3 ? "biomeScannerUltimate" : "biomeScannerBasic", Material.IRON);
        type = i;
        setCreativeTab(tab);
        setHardness(Blocks.IRON_BLOCK.getBlockHardness(null, null, null));
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
