package eladkay.scanner.blocks;

import eladkay.scanner.client.gui.GuiBiomeScanner;
import eladkay.scanner.tiles.TileEntityBiomeScanner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockBiomeScanner extends Block {
    int type;

    public BlockBiomeScanner(int i) {
        super(Properties.of(Material.METAL).strength(5));
        type = i;
        switch (type) {
            case 0:
            default:
                //setUnlocalizedName("biomeScannerBasic");
                //setRegistryName("biome_scanner_basic");
                break;
            case 1:
                //setUnlocalizedName("biomeScannerAdv");
                //setRegistryName("biome_scanner_advanced");
                break;
            case 2:
                //setUnlocalizedName("biomeScannerElite");
                //setRegistryName("biome_scanner_elite");
                break;
            case 3:
                //setUnlocalizedName("biomeScannerUltimate");
                //setRegistryName("biome_scanner_ultimate");
        }
        //setCreativeTab(TAB);
        //setHardness(5);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityBiomeScanner();
    }

    @Override
    public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand, BlockRayTraceResult pHit) {
        System.out.println("0");
        if(!pLevel.isClientSide) return ActionResultType.PASS;
        System.out.println("1");
        if (pLevel.getBlockEntity(pPos) != null) {
            System.out.println("2");
            new GuiBiomeScanner((TileEntityBiomeScanner) pLevel.getBlockEntity(pPos)).openGui();
        }
        return ActionResultType.SUCCESS;
    }
}
