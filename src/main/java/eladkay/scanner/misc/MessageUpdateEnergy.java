package eladkay.scanner.misc;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import eladkay.scanner.ScannerMod;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@PacketRegister(Side.CLIENT)
public class MessageUpdateEnergy extends PacketBase {

    @Save
    int x;
    @Save
    int y;
    @Save
    int z;
    @Save
    long energy;
    @Save
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
    public void handle(MessageContext player) {
        World server = ScannerMod.proxy.getWorld();
        BaseTE base = (BaseTE) server.getTileEntity(new BlockPos(x, y, z));
        //System.out.println(Minecraft.getMinecraft().world.getBlockState(new BlockPos(message.x, message.y, message.z)).getBlock());
        //System.out.println(new BlockPos(message.x, message.y, message.z));
        if (base != null && base.container() != null)
            base.container().setEnergyStored(energy);
    }
}
