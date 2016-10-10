package eladkay.scanner.biome;


import eladkay.scanner.misc.MessageBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class MessageUpdateMap extends MessageBase<MessageUpdateMap> {

    int x;
    int y;
    int z;
    int chunkX;
    int chunkY;
    int powerCost;

    public MessageUpdateMap() {

    }

    public MessageUpdateMap(int x, int y, int z, int chunkX, int chunkY, int powerCost) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.powerCost = powerCost;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        chunkX = buf.readInt();
        chunkY = buf.readInt();
        powerCost = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(chunkX);
        buf.writeInt(chunkY);
        buf.writeInt(powerCost);
    }

    @Override
    public void handleClientSide(MessageUpdateMap message, EntityPlayer player) {
    } //noop

    @Override
    public void handleServerSide(MessageUpdateMap message, EntityPlayer player) {
        TileEntityBiomeScanner bs = (TileEntityBiomeScanner) player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
        bs.container().extractEnergy(message.powerCost, false);
        bs.mapping.put(new ChunkPos(message.chunkX, message.chunkY), player.worldObj.getBiomeGenForCoords(new BlockPos(message.chunkX * 16 + 8, 0, message.chunkY * 16 + 8)).getBiomeName());
        bs.markDirty();
    }
}
