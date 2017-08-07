package eladkay.scanner.biome;


import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import eladkay.scanner.Config;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@PacketRegister(Side.SERVER)
public class MessageUpdateMap extends PacketBase {

    @Save
    private int x;
    @Save
    private int y;
    @Save
    private int z;
    @Save
    private int chunkX;
    @Save
    private int chunkY;

    public MessageUpdateMap() {

    }

    public MessageUpdateMap(TileEntityBiomeScanner scanner, int chunkX, int chunkY) {
        this.x = scanner.getPos().getX();
        this.y = scanner.getPos().getY();
        this.z = scanner.getPos().getZ();
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }


    @Override
    public void handle(MessageContext player) {

        TileEntityBiomeScanner bs = (TileEntityBiomeScanner) player.getServerHandler().playerEntity.world.getTileEntity(new BlockPos(x, y, z));
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkY);
        int powerCost = Config.minEnergyPerChunkBiomeScanner * Config.increase * bs.getDist(chunkPos);
        bs.getContainer().extractEnergy(powerCost, false);
        bs.mapping.put(chunkPos, player.getServerHandler().playerEntity.world.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkY * 16 + 8)).getBiomeName());
        bs.markDirty();
    }
}
