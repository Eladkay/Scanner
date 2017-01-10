package eladkay.scanner.misc;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.stream.Collectors;

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
        if (base != null)
            NetworkHelper.tellEveryone(new MessageUpdateEnergy(message.x, message.y, message.z, base.getEnergyStored(null), server.provider.getDimension()));
        try {
            for (BlockPos pos : Lists.newArrayList(EnumFacing.values()).stream().map((it) -> new BlockPos(message.x, message.y, message.z).offset(it)).collect(Collectors.toList())) {
                //server.scheduleBlockUpdate(pos, server.getBlockState(pos).getBlock(), 1, 50000);
                //server.notifyBlockUpdate(pos, server.getBlockState(pos), server.getBlockState(pos), 3);
            }
        } catch (Exception e) {
            //NO-OP
        }

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
