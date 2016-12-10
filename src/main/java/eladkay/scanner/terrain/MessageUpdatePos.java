package eladkay.scanner.terrain;

import eladkay.scanner.misc.MessageBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdatePos extends MessageBase<MessageUpdatePos> {
    private int x;
    private int y;
    private int z;
    private long pos;

    public MessageUpdatePos() {

    }

    public MessageUpdatePos(TileEntityTerrainScanner scanner, BlockPos pos) {
        this.x = scanner.getPos().getX();
        this.y = scanner.getPos().getY();
        this.z = scanner.getPos().getZ();
        this.pos = pos.toLong();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        pos = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeLong(pos);
    }

    @Override
    public void handleClientSide(MessageUpdatePos message, EntityPlayer player) {
    } //noop

    @Override
    public void handleServerSide(MessageUpdatePos message, MessageContext player) {

        TileEntityTerrainScanner bs = (TileEntityTerrainScanner) player.getServerHandler().playerEntity.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
        bs.posStart = BlockPos.fromLong(message.pos);
        bs.current = new BlockPos.MutableBlockPos(0, -1, 0);
        bs.markDirty();
    }
}
