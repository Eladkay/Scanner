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
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

        ModBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModTileEntities.TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModContainerTypes.CONTAINER_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register ourselves for server and other game events we are interested in
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
                    System.out.println("Client side work shit");
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
            // Get some important fields
            final IServerConfiguration serverConfig = server.getWorldData();
            final DimensionGeneratorSettings dimensionSettings = serverConfig.worldGenSettings();
            final DynamicRegistries registries = server.registryAccess();
            final IChunkStatusListener chunkStatusListener = server.progressListenerFactory.create(11);

            // Register world
            if(!ScannerConfig.CONFIG.dimensionBlacklist.get().contains("minecraft:overworld")) {
                worlds.put(ModDimensions.FAKE_OVERWORLD, generateScannerOverworld(server, serverConfig, dimensionSettings, registries, chunkStatusListener));
                MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(worlds.get(ModDimensions.FAKE_OVERWORLD)));
            }

            Set<Map.Entry<RegistryKey<Dimension>, Dimension>> set = new HashSet<>(dimensionSettings.dimensions().entrySet());

            for(Map.Entry<RegistryKey<Dimension>, Dimension> entry : set) {
                RegistryKey<Dimension> registrykey = entry.getKey();
                if(ScannerConfig.CONFIG.dimensionBlacklist.get().contains(registrykey.location().toString())) continue;
                if (registrykey != Dimension.OVERWORLD && !registrykey.location().getNamespace().equals(ScannerMod.MODID)) {
                    Pair<RegistryKey<World>, ServerWorld> pair = generateOther(entry, server, serverConfig, dimensionSettings, registries, chunkStatusListener);
                    worlds.put(pair.getFirst(), pair.getSecond());
                    MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(worlds.get(pair.getFirst())));
                }
            }

            server.markWorldsDirty();
        }
    }

    private ServerWorld generateScannerOverworld(MinecraftServer server, IServerConfiguration serverConfig, DimensionGeneratorSettings dimensionSettings, DynamicRegistries registries, IChunkStatusListener chunkStatusListener) {
        final long seed = dimensionSettings.seed();
        // Create chunk generator
        final ChunkGenerator generator = DimensionGeneratorSettings.makeDefaultOverworld(registries.registryOrThrow(Registry.BIOME_REGISTRY), registries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY), seed);
        // Create dimension
        final Dimension dimension = new Dimension(() -> DimensionType.DEFAULT_OVERWORLD, generator);
        // Register dimension
        dimensionSettings.dimensions().register(ModDimensions.FAKE_OVERWORLD_DIMENSION, dimension, Lifecycle.experimental());
        // Create world
        final DerivedWorldInfo worldInfo = new DerivedWorldInfo(serverConfig, serverConfig.overworldData());
        final ServerWorld world = new ServerWorld(server, server.executor, server.storageSource, worldInfo, ModDimensions.FAKE_OVERWORLD, dimension.type(), chunkStatusListener, dimension.generator(), dimensionSettings.isDebug(), BiomeManager.obfuscateSeed(seed), ImmutableList.of(), false);
        return world;
    }

    private Pair<RegistryKey<World>, ServerWorld> generateOther(Map.Entry<RegistryKey<Dimension>, Dimension> entry, MinecraftServer server, IServerConfiguration serverConfig, DimensionGeneratorSettings dimensionSettings, DynamicRegistries registries, IChunkStatusListener chunkStatusListener) {
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

    /*@SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.registerServerCommand(new SpeedTickCommand());
        event.registerServerCommand(new TpToDim99Command());
    }

    public static class SpeedTickCommand extends CommandBase {


        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            return sender.getName().matches("(?:Player\\d{1,3})|(?:Eladk[ae]y)|(IGCBOOM)|(MisterPlus)");
        }

        @Override
        public String getName() {
            return "speedts";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "/speedts";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            TileEntity te = server.getWorld(getCommandSenderAsPlayer(sender).dimension).getTileEntity(getCommandSenderAsPlayer(sender).getPosition().down());
            if (te instanceof ITickable) for (int i = 0; i < 100000; i++) ((ITickable) te).update();

        }
    }

    public static class TpToDim99Command extends CommandBase {


        @Override
        public String getName() {
            return "goto";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "/goto";
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            return sender.getName().matches("(?:Player\\d{1,3})|(?:Eladk[ae]y)|(IGCBOOM)|(Misterplus)");
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length != 1) return;
            if ("homepls".equals(args[0]))
                getCommandSenderAsPlayer(sender).changeDimension(0);
            else if (args[0].contains("offwego"))
                getCommandSenderAsPlayer(sender).changeDimension(Integer.parseInt(args[0].replace("offwego", "")));

        }
    }*/
}