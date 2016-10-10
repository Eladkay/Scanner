package eladkay.scanner.terrain;

import eladkay.scanner.Config;
import eladkay.scanner.ScannerMod;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static eladkay.scanner.ScannerMod.tab;

public class BlockTerrainScanner extends Block implements ITileEntityProvider {
    public BlockTerrainScanner() {
        super(Material.IRON);
        setRegistryName(ScannerMod.MODID + ":terrainScanner");
        setUnlocalizedName("terrainScanner");
        setCreativeTab(tab);
        setHardness(1);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTerrainScanner();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        ((TileEntityTerrainScanner) worldIn.getTileEntity(pos)).onBlockActivated();
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState bs) {
        //We need to do this always in case the config has changed seince the block as added.
        BlockPos start = pos.east().down(pos.getY());
        for (BlockPos p : BlockPos.MutableBlockPos.getAllInBoxMutable(start, start.add(15, 255, 15))) {
            IBlockState state = worldIn.getBlockState(p);
            if (state.getBlock() == ScannerMod.air)
                worldIn.setBlockState(p, Blocks.AIR.getDefaultState());
        }
        super.breakBlock(worldIn, pos, bs);
    }

    @Override
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {   //We need to do this always in case the config has changed seince the block as added.
        BlockPos start = pos.east().down(pos.getY());
        for (BlockPos p : BlockPos.MutableBlockPos.getAllInBoxMutable(start, start.add(15, 255, 15))) {
            IBlockState state = worldIn.getBlockState(p);
            if (state.getBlock() == ScannerMod.air)
                worldIn.setBlockState(p, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public IBlockState onBlockPlaced(World worldObj, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if (Config.showOutline) {
            BlockPos start = pos.east().down(pos.getY());
            for (BlockPos p : BlockPos.MutableBlockPos.getAllInBoxMutable(start, start.add(15, 255, 15))) {
                IBlockState state = worldObj.getBlockState(pos);
                if (state.getBlock().isReplaceable(worldObj, p) || state.getBlock().isAir(state, worldObj, p))
                    worldObj.setBlockState(p, ScannerMod.air.getDefaultState());
            }
        }
        return super.onBlockPlaced(worldObj, pos, facing, hitX, hitY, hitZ, meta, placer);
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

    public static PropertyBool ONOFF = PropertyBool.create("state");


    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
}
