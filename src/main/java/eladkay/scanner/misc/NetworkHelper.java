package eladkay.scanner.misc;

import eladkay.scanner.ScannerMod;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class NetworkHelper {
    public static SimpleNetworkWrapper instance;

    public static void init() {
        instance = new SimpleNetworkWrapper(ScannerMod.MODID);
        try {
            //instance.registerMessage();
        } catch (NoClassDefFoundError server) {
            server.printStackTrace();
        }
        //todo this is bad
        //Caused by: java.lang.NoClassDefFoundError: net/minecraft/client/entity/EntityPlayerSP
    }

    //lol
    public static void tellEveryone(IMessage message) {
        instance.sendToAll(message);
    }

    public static void tellEveryoneAround(IMessage message, int dim, int x, int y, int z, int range) {
        instance.sendToAllAround(message, new NetworkRegistry.TargetPoint(dim, x, y, z, range));
    }

    public static void tellEveryoneAround(IMessage message, int dim, BlockPos pos, int range) {
        instance.sendToAllAround(message, new NetworkRegistry.TargetPoint(dim, pos.getX(), pos.getY(), pos.getZ(), range));
    }

    public static void tellEveryoneAround(IMessage message, NetworkRegistry.TargetPoint point) {
        instance.sendToAllAround(message, point);
    }
}
