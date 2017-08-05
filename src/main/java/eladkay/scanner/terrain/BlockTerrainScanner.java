package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.librarianlib.common.base.block.ItemModBlock;
import eladkay.scanner.Config;
import eladkay.scanner.ScannerMod;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static eladkay.scanner.ScannerMod.tab;

public class BlockTerrainScanner extends BlockModContainer {
    public static PropertyBool ONOFF = PropertyBool.create("state");

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemBlock createItemForm() {
        return new ItemModBlock(this) {
            @Override
            public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
                tooltip.add("The ultimate terrain reconstruction tool.");
                tooltip.add("Its GUI is fairly self-explanatory in my opinion.");
                super.addInformation(stack, playerIn, tooltip, advanced);
            }
        };
    }

    public BlockTerrainScanner() {
        super("terrainScanner", Material.IRON);
        setCreativeTab(tab);
        setHardness(Blocks.IRON_BLOCK.getBlockHardness(null, null, null));
    }

    @Override
    public TileEntity createTileEntity(World worldIn, IBlockState meta) {
        return new TileEntityTerrainScanner();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        ScannerMod.proxy.openGuiTerrainScanner(((TileEntityTerrainScanner) worldIn.getTileEntity(pos)));
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState bs) {
        //We need to do this always in case the config has changed seince the block as added.
        BlockPos start = pos.east().down(pos.getY());
        for (BlockPos p : BlockPos.MutableBlockPos.getAllInBoxMutable(start, start.add(15, 255, 15))) {
            IBlockState state = worldIn.getBlockState(p);
            if (state.getBlock().isAir(state, worldIn, p))
                worldIn.setBlockState(p, Blocks.AIR.getDefaultState());
        }
        super.breakBlock(worldIn, pos, bs);
    }

    @Override
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {   //We need to do this always in case the config has changed seince the block as added.
        BlockPos start = pos.east().down(pos.getY());
        for (BlockPos p : BlockPos.MutableBlockPos.getAllInBoxMutable(start, start.add(15, 255, 15))) {
            IBlockState state = worldIn.getBlockState(p);
            if (state.getBlock().isAir(state, worldIn, p))
                worldIn.setBlockState(p, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
        onBlockDestroyedByExplosion(worldIn, pos, null);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ONOFF);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return ((TileEntityTerrainScanner) worldIn.getTileEntity(pos)).on ? state.withProperty(ONOFF, true) : state.withProperty(ONOFF, false);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
}
