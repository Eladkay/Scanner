package eladkay.scanner.networking;


import eladkay.scanner.ScannerConfig;
import eladkay.scanner.tiles.TileEntityBiomeScanner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageUpdateMap extends MessageBase<MessageUpdateMap> {

    private int x;
    private int y;
    private int z;
    private int chunkX;
    private int chunkY;

    public MessageUpdateMap(PacketBuffer buf) {
        fromBuffer(buf);
    }

    public MessageUpdateMap(TileEntityBiomeScanner scanner, int chunkX, int chunkY) {
        this.x = scanner.getBlockPos().getX();
        this.y = scanner.getBlockPos().getY();
        this.z = scanner.getBlockPos().getZ();
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }

    @Override
    public void fromBuffer(PacketBuffer buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        chunkX = buf.readInt();
        chunkY = buf.readInt();
    }

    @Override
    public void toBuffer(PacketBuffer buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(chunkX);
        buf.writeInt(chunkY);
    }

    @Override
    public void handleClientSide(MessageUpdateMap message, PlayerEntity player) {
    } //noop

    @Override
    public void handleServerSide(MessageUpdateMap message, NetworkEvent.Context context) {
        TileEntityBiomeScanner bs = (TileEntityBiomeScanner) context.getSender().level.getBlockEntity(new BlockPos(message.x, message.y, message.z));
        ChunkPos chunkPos = new ChunkPos(message.chunkX, message.chunkY);
        int powerCost = ScannerConfig.CONFIG.minEnergyPerChunkBiomeScanner.get() * ScannerConfig.CONFIG.increase.get() * bs.getDist(chunkPos);
        bs.container().extractEnergy(powerCost, false);
        Biome biome = context.getSender().level.getBiome(new BlockPos(message.chunkX * 16 + 8, 0, message.chunkY * 16 + 8));
        ResourceLocation resourceLocation = context.getSender().level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
        String s = "biome." + resourceLocation.getNamespace() + "." + resourceLocation.getPath();
        bs.mapping.put(chunkPos, new TranslationTextComponent(s));
        bs.setChanged();
    }
}
