package eladkay.scanner.init;

import eladkay.scanner.ScannerMod;
import eladkay.scanner.blocks.BlockBiomeScanner;
import eladkay.scanner.blocks.BlockScannerQueue;
import eladkay.scanner.blocks.BlockTerrainScanner;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {

    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ScannerMod.MODID);
    public static RegistryObject<BlockTerrainScanner> TERRAIN_SCANNER = BLOCKS.register("terrain_scanner", BlockTerrainScanner::new);
    public static RegistryObject<BlockScannerQueue> SCANNER_QUEUE = BLOCKS.register("scanner_queue", BlockScannerQueue::new);
    public static RegistryObject<BlockBiomeScanner> BIOME_SCANNER_BASIC = BLOCKS.register("biome_scanner_basic", () -> new BlockBiomeScanner(0));
    public static RegistryObject<BlockBiomeScanner> BIOME_SCANNER_ADVANCED = BLOCKS.register("biome_scanner_advanced", () -> new BlockBiomeScanner(1));
    public static RegistryObject<BlockBiomeScanner> BIOME_SCANNER_ELITE = BLOCKS.register("biome_scanner_elite", () -> new BlockBiomeScanner(2));
    public static RegistryObject<BlockBiomeScanner> BIOME_SCANNER_ULTIMATE = BLOCKS.register("biome_scanner_ultimate", () -> new BlockBiomeScanner(3));

}
