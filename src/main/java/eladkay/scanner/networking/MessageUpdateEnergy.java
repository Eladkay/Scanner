package eladkay.scanner.networking;

import eladkay.scanner.tiles.BaseTE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageUpdateEnergy extends MessageBase<MessageUpdateEnergy> {

    int x;
    int y;
    int z;
    int energy;

    public MessageUpdateEnergy(int x, int y, int z, int energy) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.energy = energy;
    }

    public MessageUpdateEnergy(PacketBuffer buf) {
        fromBuffer(buf);
    }

    @Override
    public void fromBuffer(PacketBuffer buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        energy = buf.readInt();
    }

    @Override
    public void toBuffer(PacketBuffer buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(energy);
    }

    @Override
    public void handleClientSide(MessageUpdateEnergy message, PlayerEntity player) {
        BaseTE base = (BaseTE) player.level.getBlockEntity(new BlockPos(message.x, message.y, message.z));
        //System.out.println(Minecraft.getMinecraft().world.getBlockState(new BlockPos(message.x, message.y, message.z)).getBlock());
        //System.out.println(new BlockPos(message.x, message.y, message.z));
        if (base != null && base.container != null)
            base.container.setEnergyStored(message.energy);
    }

    @Override
    public void handleServerSide(MessageUpdateEnergy message, NetworkEvent.Context context) {

    }
}
