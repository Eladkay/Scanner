package eladkay.scanner.terrain;

import eladkay.scanner.Config;
import eladkay.scanner.ScannerMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
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
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
        if(!Config.showOutline) return;
        for(int x0 = 0; x0 < 16; x0++)
            for(int y0 = 0; y0 < 256; y0++)
                for(int z0 = 0; z0 < 16; z0++)
                    if(worldIn.getBlockState(new BlockPos(x0 + pos.getX() + 1, y0, z0 + pos.getZ())).getBlock() == ScannerMod.air)
                        worldIn.setBlockState(new BlockPos(pos.getX() + x0 + 1, y0, pos.getZ() + z0), Blocks.AIR.getDefaultState());
    }

    @Override
    public IBlockState onBlockPlaced(World worldObj, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if(Config.showOutline)
            for(int x0 = 0; x0 < 16; x0++)
                for(int y0 = 0; y0 < 256; y0++)
                    for(int z0 = 0; z0 < 16; z0++)
                        if(worldObj.getBlockState(new BlockPos(x0 + pos.getX() + 1, y0, z0 + pos.getZ())).getBlock()
                                .isReplaceable(worldObj, new BlockPos(x0 + pos.getX() + 1, y0, z0 + pos.getZ())) ||
                                worldObj.getBlockState(new BlockPos(x0 + pos.getX() + 1, y0, z0 + pos.getZ())).getBlock() instanceof BlockAir)
                            worldObj.setBlockState(new BlockPos(pos.getX() + x0 + 1, y0, pos.getZ() + z0), ScannerMod.air.getDefaultState());
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
        return ((TileEntityTerrainScanner)worldIn.getTileEntity(pos)).on ? state.withProperty(ONOFF, EnumType.ON) : state.withProperty(ONOFF, EnumType.OFF);
    }

    public static PropertyEnum<EnumType> ONOFF = PropertyEnum.create("state", EnumType.class);

    public enum EnumType implements IStringSerializable {
        ON {
            @Override
            public String getName() {
                return "on";
            }
        },
        OFF {
            @Override
            public String getName() {
                return "off";
            }
        };
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
}
