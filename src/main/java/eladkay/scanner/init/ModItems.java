package eladkay.scanner.init;

import eladkay.scanner.ScannerMod;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

    public final static ItemGroup TAB = new ScannerCreativeTabs(ScannerMod.MODID);

    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ScannerMod.MODID);
    public static RegistryObject<BlockItem> TERRAIN_SCANNER = ITEMS.register("terrain_scanner", () -> new BlockItem(ModBlocks.TERRAIN_SCANNER.get(), new Item.Properties().tab(TAB)));
    public static RegistryObject<BlockItem> SCANNER_QUEUE = ITEMS.register("scanner_queue", () -> new BlockItem(ModBlocks.SCANNER_QUEUE.get(), new Item.Properties().tab(TAB)));
    public static RegistryObject<BlockItem> BIOME_SCANNER_BASIC = ITEMS.register("biome_scanner_basic", () -> new BlockItem(ModBlocks.BIOME_SCANNER_BASIC.get(), new Item.Properties().tab(TAB)));
    public static RegistryObject<BlockItem> BIOME_SCANNER_ADVANCED = ITEMS.register("biome_scanner_advanced", () -> new BlockItem(ModBlocks.BIOME_SCANNER_ADVANCED.get(), new Item.Properties().tab(TAB)));
    public static RegistryObject<BlockItem> BIOME_SCANNER_ELITE = ITEMS.register("biome_scanner_elite", () -> new BlockItem(ModBlocks.BIOME_SCANNER_ELITE.get(), new Item.Properties().tab(TAB)));
    public static RegistryObject<BlockItem> BIOME_SCANNER_ULTIMATE = ITEMS.register("biome_scanner_ultimate", () -> new BlockItem(ModBlocks.BIOME_SCANNER_ULTIMATE.get(), new Item.Properties().tab(TAB)));

}
