package eladkay.scanner.misc;

import eladkay.scanner.ScannerMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdateEnergy extends MessageBase<MessageUpdateEnergy> {

    int x;
    int y;
    int z;
    long energy;
    int dim;

    public MessageUpdateEnergy(int x, int y, int z, long energy, int dim) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.energy = energy;
        this.dim = dim;
    }

    public MessageUpdateEnergy() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        energy = buf.readLong();
        dim = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeLong(energy);
        buf.writeInt(dim);
    }

    @Override
    public void handleClientSide(MessageUpdateEnergy message, EntityPlayer player) {
        World server = ScannerMod.proxy.getWorld();
        BaseTE base = (BaseTE) server.getTileEntity(new BlockPos(message.x, message.y, message.z));
        //System.out.println(Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(message.x, message.y, message.z)).getBlock());
        //System.out.println(new BlockPos(message.x, message.y, message.z));
        if (base != null)
            base.container.setEnergyStored(message.energy);
    }

    @Override
    public void handleServerSide(MessageUpdateEnergy message, MessageContext player) {

    }
}
