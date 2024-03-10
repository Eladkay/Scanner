package eladkay.scanner.networking;

import eladkay.scanner.ScannerMod;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.List;

public class NetworkHelper {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ScannerMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void init() {
        INSTANCE.registerMessage(++id, MessageUpdateMap.class, MessageUpdateMap::toBuffer, MessageUpdateMap::new, MessageBase::onMessage);
        INSTANCE.registerMessage(++id, MessageUpdateEnergy.class, MessageUpdateEnergy::toBuffer, MessageUpdateEnergy::new, MessageBase::onMessage);
        INSTANCE.registerMessage(++id, MessageUpdateEnergyServer.class, MessageUpdateEnergyServer::toBuffer, MessageUpdateEnergyServer::new, MessageBase::onMessage);
        INSTANCE.registerMessage(++id, MessageUpdateScanner.class, MessageUpdateScanner::toBuffer, MessageUpdateScanner::new, MessageBase::onMessage);
        INSTANCE.registerMessage(++id, MessageUpdateQueue.class, MessageUpdateQueue::toBuffer, MessageUpdateQueue::new, MessageBase::onMessage);
    }

    public static void broadcast(MinecraftServer server, MessageBase<?> message) {
        List<ServerPlayerEntity> players = server.getPlayerList().getPlayers();
        for(ServerPlayerEntity player : players) {
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
        }
    }

    public static void broadcastInLevel(ServerWorld level, MessageBase<?> message) {
        List<ServerPlayerEntity> players = level.getPlayers((player) -> true);
        for(ServerPlayerEntity player : players) {
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
        }
    }
}
