package eladkay.scanner;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ScannerMod.MODID)
public class ScannerMod {
    public static final String MODID = "scanner";

    @SidedProxy(serverSide = "eladkay.scanner.CommonProxy", clientSide = "eladkay.scanner.ClientProxy")
    public static CommonProxy proxy;

    public static ScannerBlock scanner;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        GameRegistry.register(scanner = new ScannerBlock());
        GameRegistry.register(new ItemBlock(scanner).setRegistryName(MODID + ":scanner"));
        GameRegistry.registerTileEntity(TileEntityScanner.class, "scanner");
        FMLInterModComms.sendMessage("Waila", "register", "eladkay.scanner.Waila.onWailaCall");
        proxy.init();

        Config.initConfig(event.getSuggestedConfigurationFile());
    }
}
