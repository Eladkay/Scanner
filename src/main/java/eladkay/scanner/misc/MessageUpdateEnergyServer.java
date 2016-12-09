package eladkay.scanner.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdateEnergyServer extends MessageBase<MessageUpdateEnergyServer> {
    int x;
    int y;
    int z;

    public MessageUpdateEnergyServer(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public MessageUpdateEnergyServer() {

    }

    /**
     * Handle a packet on the client side. Note this occurs after decoding has completed.
     *
     * @param message the packet
     * @param player  the player reference
     */
    @Override
    public void handleClientSide(MessageUpdateEnergyServer message, EntityPlayer player) {

    }

    /**
     * Handle a packet on the server side. Note this occurs after decoding has completed.
     *
     * @param message the packet
     * @param player  the player reference
     */
    @Override
    public void handleServerSide(MessageUpdateEnergyServer message, MessageContext player) {
        World server = player.getServerHandler().playerEntity.worldObj;
        BaseTE base = (BaseTE) server.getTileEntity(new BlockPos(message.x, message.y, message.z));
        if (base == null) System.out.println(new BlockPos(message.x, message.y, message.z));
        NetworkHelper.tellEveryone(new MessageUpdateEnergy(message.x, message.y, message.z, base.getEnergyStored(null), server.provider.getDimension()));
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
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
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }
}
