package eladkay.scanner.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class MessageUpdateEnergy extends MessageBase<MessageUpdateEnergy> {

    int x;
    int y;
    int z;
    long energy;

    public MessageUpdateEnergy(int x, int y, int z, long energy) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.energy = energy;
    }

    public MessageUpdateEnergy() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        energy = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeLong(energy);
    }

    @Override
    public void handleClientSide(MessageUpdateEnergy message, EntityPlayer player) {
        BaseTE te = (BaseTE) Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(message.x, message.y, message.z));
        //System.out.println(Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(message.x, message.y, message.z)).getBlock());
        //System.out.println(new BlockPos(message.x, message.y, message.z));
        if (te != null)
            te.container.setStored(message.energy);
    }

    @Override
    public void handleServerSide(MessageUpdateEnergy message, EntityPlayer player) {

    }
}
