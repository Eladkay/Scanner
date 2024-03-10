package eladkay.scanner.networking;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageUpdateScanner extends MessageBase<MessageUpdateScanner> {
    private int x;
    private int y;
    private int z;
    private CompoundNBT data;

    public MessageUpdateScanner(TileEntity scanner) {
        this.x = scanner.getBlockPos().getX();
        this.y = scanner.getBlockPos().getY();
        this.z = scanner.getBlockPos().getZ();
        this.data = scanner.getUpdateTag();
    }

    public MessageUpdateScanner(PacketBuffer buf) {
        fromBuffer(buf);
    }

    @Override
    public void fromBuffer(PacketBuffer buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        data = buf.readNbt();
    }

    @Override
    public void toBuffer(PacketBuffer buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeNbt(data);
    }

    @Override
    public void handleClientSide(MessageUpdateScanner message, PlayerEntity player) {
        TileEntity bs = player.level.getBlockEntity(new BlockPos(message.x, message.y, message.z));
        if (bs == null) return;
        bs.load(bs.getBlockState(), message.data);
        bs.setChanged();
    }

    @Override
    public void handleServerSide(MessageUpdateScanner message, NetworkEvent.Context context) {
        TileEntity bs = context.getSender().level.getBlockEntity(new BlockPos(message.x, message.y, message.z));
        if (bs == null) return;
        bs.load(bs.getBlockState(), message.data);
        bs.setChanged();
    }

    public static void send(TileEntity scanner) {
            if(scanner.getLevel().isClientSide) {
                NetworkHelper.INSTANCE.sendToServer(new MessageUpdateScanner(scanner));
            } else {
                NetworkHelper.broadcast(scanner.getLevel().getServer(), new MessageUpdateScanner(scanner));
            }
    }
}
