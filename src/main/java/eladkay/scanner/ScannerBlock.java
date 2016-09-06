package eladkay.scanner;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ScannerBlock extends Block implements ITileEntityProvider {
    public ScannerBlock() {
        super(Material.IRON);
        setRegistryName(ScannerMod.MODID + ":scanner");
    }


    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityScanner();
    }
}
