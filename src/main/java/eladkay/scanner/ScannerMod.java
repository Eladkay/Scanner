package eladkay.scanner;

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

    @SidedProxy(serverSide = "eladkay.scanner.CommonProxy", clientSide = "eladkay.scanner.ClientProxy")
    public static CommonProxy proxy;

    public static DimensionType dim;
    public static ScannerBlock scanner;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        GameRegistry.register(scanner = new ScannerBlock());
        GameRegistry.register(new ItemBlock(scanner).setRegistryName(MODID + ":scanner"));
        GameRegistry.registerTileEntity(TileEntityScanner.class, "scanner");
        FMLInterModComms.sendMessage("Waila", "register", "eladkay.scanner.Waila.onWailaCall");
        MineTweaker.init();
        proxy.init();
        Config.initConfig(event.getSuggestedConfigurationFile());
        dim = DimensionType.register("fakeoverworld", "", Config.dimid, WorldProviderOverworld.class, true);
        DimensionManager.registerDimension(Config.dimid, dim);
    }
    public static class WorldProviderOverworld extends WorldProvider {

        @Override
        public DimensionType getDimensionType() {
            return dim;
        }

        @Override
        public IChunkGenerator createChunkGenerator() {
            return new ChunkProviderOverworld(worldObj, worldObj.getSeed(), worldObj.getWorldInfo().isMapFeaturesEnabled(), TileEntityScanner.PRESET);
        }
    }
}
