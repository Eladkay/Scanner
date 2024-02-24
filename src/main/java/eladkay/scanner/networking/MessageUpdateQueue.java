package eladkay.scanner.networking;

import eladkay.scanner.tiles.TileEntityScannerQueue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageUpdateQueue extends MessageBase<MessageUpdateQueue> {

    private BlockPos blockPos;
    private CompoundNBT compoundNBT;

    public MessageUpdateQueue(TileEntityScannerQueue queue) {
        blockPos = queue.getBlockPos();
        compoundNBT = queue.save(new CompoundNBT());
    }

    public MessageUpdateQueue(PacketBuffer buf) {
        fromBuffer(buf);
    }

    @Override
    public void fromBuffer(PacketBuffer buf) {
        blockPos = buf.readBlockPos();
        compoundNBT = buf.readNbt();
    }

    @Override
    public void toBuffer(PacketBuffer buf) {
        buf.writeBlockPos(blockPos);
        buf.writeNbt(compoundNBT);
    }

    @Override
    public void handleClientSide(MessageUpdateQueue message, PlayerEntity player) {
        TileEntity bs = player.level.getBlockEntity(message.blockPos);
        if (bs == null) return;
        if(!(bs instanceof TileEntityScannerQueue)) return;
        bs.load(bs.getBlockState(), message.compoundNBT);
        bs.setChanged();
    }

    @Override
    public void handleServerSide(MessageUpdateQueue message, NetworkEvent.Context context) {

    }
}
