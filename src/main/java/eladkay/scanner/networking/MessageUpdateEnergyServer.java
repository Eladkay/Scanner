package eladkay.scanner.networking;

import eladkay.scanner.tiles.BaseTE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageUpdateEnergyServer extends MessageBase<MessageUpdateEnergyServer> {
    int x;
    int y;
    int z;

    public MessageUpdateEnergyServer(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public MessageUpdateEnergyServer(PacketBuffer buf) {
        fromBuffer(buf);
    }

    @Override
    public void handleClientSide(MessageUpdateEnergyServer message, PlayerEntity player) {

    }

    @Override
    public void handleServerSide(MessageUpdateEnergyServer message, NetworkEvent.Context context) {
        ServerWorld level = (ServerWorld) context.getSender().level;
        BaseTE base = (BaseTE) level.getBlockEntity(new BlockPos(message.x, message.y, message.z));
        if (base != null)
            NetworkHelper.broadcastInLevel(level, new MessageUpdateEnergy(message.x, message.y, message.z, base.getEnergyStored()));
        /*try {
            for (BlockPos pos : Lists.newArrayList(EnumFacing.values()).stream().map((it) -> new BlockPos(message.x, message.y, message.z).offset(it)).collect(Collectors.toList())) {
                //server.scheduleBlockUpdate(pos, server.getBlockState(pos).getBlock(), 1, 50000);
                //server.notifyBlockUpdate(pos, server.getBlockState(pos), server.getBlockState(pos), 3);
            }
        } catch (Exception e) {
            //NO-OP
        }*/

    }

    @Override
    public void fromBuffer(PacketBuffer buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBuffer(PacketBuffer buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }
}
