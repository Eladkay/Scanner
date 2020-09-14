package eladkay.scanner.terrain;

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
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

import static eladkay.scanner.ScannerMod.TAB;

public class BlockTerrainScanner extends Block implements ITileEntityProvider {
    public static PropertyBool ONOFF = PropertyBool.create("state");

    public BlockTerrainScanner() {
        super(Material.IRON);
        setRegistryName(ScannerMod.MODID + ":terrain_scanner");
        setUnlocalizedName("terrainScanner");
        setCreativeTab(TAB);
        setHardness(5);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTerrainScanner();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ScannerMod.proxy.openGuiTerrainScanner(((TileEntityTerrainScanner) worldIn.getTileEntity(pos)));
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState bs) {
        //We need to do this always in case the config has changed since the block as added.
        BlockPos start = pos.east().down(pos.getY());
        for (BlockPos p : BlockPos.MutableBlockPos.getAllInBoxMutable(start, start.add(15, 255, 15))) {
            IBlockState state = worldIn.getBlockState(p);
            if (state.getBlock().isAir(state, worldIn, p))
                worldIn.setBlockState(p, Blocks.AIR.getDefaultState());
        }
        super.breakBlock(worldIn, pos, bs);
    }

    @Override
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {   //We need to do this always in case the config has changed since the block as added.
        BlockPos start = pos.east().down(pos.getY());
        for (BlockPos p : BlockPos.MutableBlockPos.getAllInBoxMutable(start, start.add(15, 255, 15))) {
            IBlockState state = worldIn.getBlockState(p);
            if (state.getBlock().isAir(state, worldIn, p))
                worldIn.setBlockState(p, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state0, EntityLivingBase placer, ItemStack stack) {
        /*if (Config.showOutline) {
            BlockPos start = pos.east().down(pos.getY());
            for (BlockPos p : BlockPos.MutableBlockPos.getAllInBoxMutable(start, start.add(15, 255, 15))) {
                IBlockState state = worldIn.getBlockState(p);
                if (state.getBlock() == Blocks.AIR)
                    worldIn.setBlockState(p, ScannerMod.air.getDefaultState());
            }
        }*/
        super.onBlockPlacedBy(worldIn, pos, state0, placer, stack);
        TileEntity te = worldIn.getTileEntity(pos);
        TileEntityTerrainScanner tets = ((TileEntityTerrainScanner)te);
        if (placer instanceof EntityPlayer) {
            tets.placer = placer.getUniqueID();
            EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(placer.getUniqueID());
            if (player != null)
                tets.placerName = player.getName();
            else
                tets.placerName = "";
        }
        else {
            tets.placer = new UUID(0,0);
            tets.placerName = "";
        }
        te.markDirty();
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
