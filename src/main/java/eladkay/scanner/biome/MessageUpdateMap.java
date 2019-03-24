package eladkay.scanner.biome;


import eladkay.scanner.Config;
import eladkay.scanner.misc.MessageBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdateMap extends MessageBase<MessageUpdateMap> {

    private int x;
    private int y;
    private int z;
    private int chunkX;
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
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        chunkX = buf.readInt();
        chunkY = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(chunkX);
        buf.writeInt(chunkY);
    }

    @Override
    public void handleClientSide(MessageUpdateMap message, EntityPlayer player) {
    } //noop

    @Override
    public void handleServerSide(MessageUpdateMap message, MessageContext player) {

        TileEntityBiomeScanner bs = (TileEntityBiomeScanner) player.getServerHandler().player.world.getTileEntity(new BlockPos(message.x, message.y, message.z));
        ChunkPos chunkPos = new ChunkPos(message.chunkX, message.chunkY);
        int powerCost = Config.minEnergyPerChunkBiomeScanner * Config.increase * bs.getDist(chunkPos);
        bs.container().extractEnergy(powerCost, false);
        bs.mapping.put(chunkPos, player.getServerHandler().player.world.getBiome(new BlockPos(message.chunkX * 16 + 8, 0, message.chunkY * 16 + 8)).getBiomeName());
        bs.markDirty();
    }
}
