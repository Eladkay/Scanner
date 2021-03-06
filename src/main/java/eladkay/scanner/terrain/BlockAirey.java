package eladkay.scanner.terrain;

import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockAirey extends BlockAir {
    public BlockAirey() {
        setUnlocalizedName("blockAirey");
        setRegistryName("blockAirey");
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand) {
        world.spawnParticle(EnumParticleTypes.PORTAL,
                pos.getX() + (world.rand.nextDouble() - 0.5D),
                pos.getY() + world.rand.nextDouble(),
                pos.getZ() + (world.rand.nextDouble() - 0.5D),
                (world.rand.nextDouble() - 0.5D) * 2.0D,
                -world.rand.nextDouble(),
                (world.rand.nextDouble() - 0.5D) * 2.0D);
    }
}
