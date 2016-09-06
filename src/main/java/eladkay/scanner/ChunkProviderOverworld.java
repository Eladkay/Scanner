package eladkay.scanner;

import net.minecraft.world.World;

public class ChunkProviderOverworld extends net.minecraft.world.gen.ChunkProviderOverworld {
    public ChunkProviderOverworld(World worldIn, long seed) {
        super(worldIn, seed, true, "");
    }

}
