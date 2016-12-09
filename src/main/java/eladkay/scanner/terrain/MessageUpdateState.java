package eladkay.scanner.terrain;

import eladkay.scanner.misc.MessageBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdateState extends MessageBase<MessageUpdateState> {
    boolean state;
    int x;
    int y;
    int z;

    public MessageUpdateState(boolean state, int x, int y, int z) {
        this.state = state;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public MessageUpdateState() {

    }

    /**
     * Handle a packet on the client side. Note this occurs after decoding has completed.
     *
     * @param message the packet
     * @param player  the player reference
     */
    @Override
    public void handleClientSide(MessageUpdateState message, EntityPlayer player) {

    }

    /**
     * Handle a packet on the server side. Note this occurs after decoding has completed.
     *
     * @param message the packet
     * @param player  the player reference
     */
    @Override
    public void handleServerSide(MessageUpdateState message, MessageContext player) {
        TileEntityTerrainScanner scanner = ((TileEntityTerrainScanner) player.getServerHandler().playerEntity.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z)));
        scanner.changeState(message.state);
        if (scanner.on) {
            if (scanner.pos == null) scanner.pos = scanner.getPos();
            if (scanner.current.getY() < 0) {
                scanner.current.setPos(scanner.pos.getX() + 1, 0, scanner.pos.getZ());
            } else scanner.current.setPos(scanner.pos.getX() + 1, -1, scanner.pos.getY());
        }
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        state = buf.readBoolean();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(state);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }
}
