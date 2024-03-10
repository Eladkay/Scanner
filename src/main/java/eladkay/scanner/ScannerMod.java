package eladkay.scanner;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import eladkay.scanner.compat.CraftTweaker;
import eladkay.scanner.init.*;
import eladkay.scanner.networking.NetworkHelper;
import eladkay.scanner.client.gui.GuiScannerQueue;
import eladkay.scanner.client.gui.GuiTerrainScanner;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod(ScannerMod.MODID)
public class ScannerMod {
    public static final String MODID = "scanner";
    public static final Logger LOGGER = LogManager.getLogger();
    private static final boolean TESTING = false;
    public static DimensionType dimOverWorld;
    public static DimensionType dimNether;
    public static DimensionType dimEnd;

    public ScannerMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

        ModBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModTileEntities.TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModContainerTypes.CONTAINER_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ScannerConfig.SPEC);
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        CraftTweaker.init();
        NetworkHelper.init();
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(
                () -> {
                    ScreenManager.register(ModContainerTypes.SCANNER_QUEUE_MENU.get(), GuiScannerQueue::new);
                    ScreenManager.register(ModContainerTypes.TERRAIN_SCANNER_MENU.get(), GuiTerrainScanner::new);
                }
        );
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        final MinecraftServer server = event.getServer();
        final Map<RegistryKey<World>, ServerWorld> worlds = server.forgeGetWorldMap();
        if (!worlds.containsKey(ModDimensions.FAKE_OVERWORLD)) {
            final IServerConfiguration serverConfig = server.getWorldData();
            final DimensionGeneratorSettings dimensionSettings = serverConfig.worldGenSettings();
            final DynamicRegistries registries = server.registryAccess();
            final IChunkStatusListener chunkStatusListener = server.progressListenerFactory.create(11);

            Set<Map.Entry<RegistryKey<Dimension>, Dimension>> set = new HashSet<>(dimensionSettings.dimensions().entrySet());

            for(Map.Entry<RegistryKey<Dimension>, Dimension> entry : set) {
                RegistryKey<Dimension> registrykey = entry.getKey();
                if(ScannerConfig.CONFIG.dimensionBlacklist.get().contains(registrykey.location().toString())) continue;
                if (!registrykey.location().getNamespace().equals(ScannerMod.MODID)) {
                    Pair<RegistryKey<World>, ServerWorld> pair = generateWorld(entry, server, serverConfig, dimensionSettings, registries, chunkStatusListener);
                    worlds.put(pair.getFirst(), pair.getSecond());
                    MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(worlds.get(pair.getFirst())));
                }
            }

            server.markWorldsDirty();
        }
    }

    private Pair<RegistryKey<World>, ServerWorld> generateWorld(Map.Entry<RegistryKey<Dimension>, Dimension> entry, MinecraftServer server, IServerConfiguration serverConfig, DimensionGeneratorSettings dimensionSettings, DynamicRegistries registries, IChunkStatusListener chunkStatusListener) {
        final long seed = dimensionSettings.seed();
        RegistryKey<Dimension> registrykey = entry.getKey();
        RegistryKey<World> worldKey = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(ScannerMod.MODID, registrykey.location().toString().replace(":", "_")));
        RegistryKey<Dimension> dimensionKey = RegistryKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation(ScannerMod.MODID, registrykey.location().toString().replace(":", "_")));
        DimensionType dimensionType = entry.getValue().type();

        ChunkGenerator generator = entry.getValue().generator();
        final Dimension dimension = new Dimension(() -> dimensionType, generator);
        dimensionSettings.dimensions().register(dimensionKey, dimension, Lifecycle.experimental());

        DerivedWorldInfo worldInfo = new DerivedWorldInfo(serverConfig, serverConfig.overworldData());
        ServerWorld world = new ServerWorld(server, server.executor, server.storageSource, worldInfo, worldKey, dimensionType, chunkStatusListener, dimension.generator(), dimensionSettings.isDebug(), BiomeManager.obfuscateSeed(seed), ImmutableList.of(), false);
        return new Pair<>(worldKey, world);
    }
}