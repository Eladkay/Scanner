package eladkay.scanner.biome;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static eladkay.scanner.ScannerMod.TAB;

public class BlockBiomeScanner extends Block implements ITileEntityProvider {
    int type;

    public BlockBiomeScanner(int i) {
        super(Material.IRON);
        type = i;
        switch (type) {
            case 0:
            default:
                setUnlocalizedName("biomeScannerBasic");
                setRegistryName("biome_scanner_basic");
                break;
            case 1:
                setUnlocalizedName("biomeScannerAdv");
                setRegistryName("biome_scanner_adv");
                break;
            case 2:
                setUnlocalizedName("biomeScannerElite");
                setRegistryName("biome_scanner_elite");
                break;
            case 3:
                setUnlocalizedName("biomeScannerUltimate");
                setRegistryName("biome_scanner_ultimate");
        }
        setCreativeTab(TAB);
        setHardness(5);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBiomeScanner();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ((TileEntityBiomeScanner) worldIn.getTileEntity(pos)).onBlockActivated(playerIn);
        return true;
    }
}
