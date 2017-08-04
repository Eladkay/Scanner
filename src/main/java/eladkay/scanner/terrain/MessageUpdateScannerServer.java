package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import eladkay.scanner.ScannerMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.server.FMLServerHandler;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.SERVER)
public class MessageUpdateScannerServer extends PacketBase {
    @Save
    private int x;
    @Save
    private int y;
    @Save
    private int z;
    @Save
    private NBTTagCompound data;

    public MessageUpdateScannerServer(TileEntity scanner) {
        this.x = scanner.getPos().getX();
        this.y = scanner.getPos().getY();
        this.z = scanner.getPos().getZ();
        this.data = scanner.writeToNBT(new NBTTagCompound());
    }

    public MessageUpdateScannerServer() {

    }


    public static void send(TileEntity scanner) {
        boolean flag;
        try {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
                FMLServerHandler.instance().getClientToServerNetworkManager();
            flag = true;
        } catch (RuntimeException missing) {
            flag = false;
        }
        if (flag) {
            if (scanner.getWorld().isRemote)
                PacketHandler.NETWORK.sendToServer(new MessageUpdateScannerServer(scanner));
            else PacketHandler.NETWORK.sendToAll(new MessageUpdateScannerServer(scanner));
        }
    }

    @Override
    public void handle(@NotNull MessageContext messageContext) {
        if (messageContext.side == Side.SERVER) {
            TileEntity bs = messageContext.getServerHandler().playerEntity.world.getTileEntity(new BlockPos(x, y, z));
            bs.readFromNBT(data);
            bs.markDirty();
        } else {
            TileEntity bs = ScannerMod.proxy.getWorld().getTileEntity(new BlockPos(x, y, z));
            bs.readFromNBT(data);
            bs.markDirty();
        }
    }
}
