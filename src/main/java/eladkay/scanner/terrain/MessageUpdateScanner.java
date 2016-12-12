package eladkay.scanner.terrain;

import eladkay.scanner.ScannerMod;
import eladkay.scanner.misc.MessageBase;
import eladkay.scanner.misc.NetworkHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

public class MessageUpdateScanner extends MessageBase<MessageUpdateScanner> {
    private int x;
    private int y;
    private int z;
    private NBTTagCompound data;

    public MessageUpdateScanner() {

    }

    public MessageUpdateScanner(TileEntity scanner) {
        this.x = scanner.getPos().getX();
        this.y = scanner.getPos().getY();
        this.z = scanner.getPos().getZ();
        this.data = scanner.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        ByteBufUtils.writeTag(buf, data);
    }

    @Override
    public void handleClientSide(MessageUpdateScanner message, EntityPlayer player) {
        TileEntity bs = ScannerMod.proxy.getWorld().getTileEntity(new BlockPos(message.x, message.y, message.z));
        bs.readFromNBT(message.data);
        bs.markDirty();
    } //noop

    @Override
    public void handleServerSide(MessageUpdateScanner message, MessageContext player) {

        TileEntity bs = player.getServerHandler().playerEntity.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
        bs.readFromNBT(message.data);
        bs.markDirty();
    }

    public static void send(TileEntity scanner) {
        boolean flag;
        try {
            if (!scanner.getWorld().isRemote)
                FMLServerHandler.instance().getClientToServerNetworkManager();
            flag = true;
        } catch (RuntimeException missing) {
            flag = false;
        }
        if (flag) NetworkHelper.instance.sendToServer(new MessageUpdateScanner(scanner));
    }
}
