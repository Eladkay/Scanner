package eladkay.scanner.misc;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.stream.Collectors;

@PacketRegister(Side.SERVER)
public class MessageUpdateEnergyServer extends PacketBase {
    @Save
    int x;
    @Save
    int y;
    @Save
    int z;

    public MessageUpdateEnergyServer(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public MessageUpdateEnergyServer() {

    }


    /**
     * Handle a packet on the server side. Note this occurs after decoding has completed.
     *
     * @param message the packet
     * @param player  the player reference
     */
    @Override
    public void handle(MessageContext player) {
        World server = player.getServerHandler().playerEntity.world;
        BaseTE base = (BaseTE) server.getTileEntity(new BlockPos(x, y, z));
        if (base != null)
            PacketHandler.NETWORK.sendToAll(new MessageUpdateEnergy(x, y, z, base.getEnergyStored(null), server.provider.getDimension()));
        try {
            for (BlockPos pos : Lists.newArrayList(EnumFacing.values()).stream().map((it) -> new BlockPos(x, y, z).offset(it)).collect(Collectors.toList())) {
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
