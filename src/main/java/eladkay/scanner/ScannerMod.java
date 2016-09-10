package eladkay.scanner;

import eladkay.scanner.biome.BlockBiomeScanner;
import eladkay.scanner.biome.TileEntityBiomeScanner;
import eladkay.scanner.compat.MineTweaker;
import eladkay.scanner.misc.NetworkHelper;
import eladkay.scanner.proxy.CommonProxy;
import eladkay.scanner.terrain.BlockTerrainScanner;
import eladkay.scanner.terrain.TileEntityTerrainScanner;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.ChunkProviderOverworld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ScannerMod.MODID, name = "Scanner")
public class ScannerMod {
    public static final String MODID = "scanner";

    @SidedProxy(serverSide = "eladkay.scanner.proxy.CommonProxy", clientSide = "eladkay.scanner.proxy.ClientProxy")
    public static CommonProxy proxy;

    public static DimensionType dim;
    public static BlockTerrainScanner terrainScanner;
    public static BlockBiomeScanner biomeScanner;
    @Mod.Instance(MODID)
    public static ScannerMod instance;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        instance = this;
        GameRegistry.register(terrainScanner = new BlockTerrainScanner());
        GameRegistry.register(new ItemBlock(terrainScanner).setRegistryName(MODID + ":terrainScanner"));
        GameRegistry.registerTileEntity(TileEntityTerrainScanner.class, "terrainScanner");

        GameRegistry.register(biomeScanner = new BlockBiomeScanner());
        GameRegistry.register(new ItemBlock(biomeScanner).setRegistryName(MODID + ":biomeScanner"));
        GameRegistry.registerTileEntity(TileEntityBiomeScanner.class, "biomeScanner");

        FMLInterModComms.sendMessage("Waila", "register", "eladkay.scanner.compat.Waila.onWailaCall");
        MineTweaker.init();
        proxy.init();
        Config.initConfig(event.getSuggestedConfigurationFile());
        dim = DimensionType.register("fakeoverworld", "", Config.dimid, WorldProviderOverworld.class, true);
        DimensionManager.registerDimension(Config.dimid, dim);
        NetworkHelper.init();
    }

    public static class WorldProviderOverworld extends WorldProvider {

        @Override
        public DimensionType getDimensionType() {
            return dim;
        }

        @Override
        public IChunkGenerator createChunkGenerator() {
            return new ChunkProviderOverworld(worldObj, worldObj.getSeed(), worldObj.getWorldInfo().isMapFeaturesEnabled(), TileEntityTerrainScanner.PRESET);
        }
    }
}

