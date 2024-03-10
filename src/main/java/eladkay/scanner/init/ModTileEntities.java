package eladkay.scanner.init;

import eladkay.scanner.ScannerMod;
import eladkay.scanner.tiles.TileEntityBiomeScanner;
import eladkay.scanner.tiles.TileEntityScannerQueue;
import eladkay.scanner.tiles.TileEntityTerrainScanner;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ScannerMod.MODID);
    public static final RegistryObject<TileEntityType<TileEntityBiomeScanner>> BIOME_SCANNER_TILE = TILE_ENTITIES.register("biome_scanner", () -> TileEntityType.Builder.of(TileEntityBiomeScanner::new, ModBlocks.BIOME_SCANNER_BASIC.get(), ModBlocks.BIOME_SCANNER_ADVANCED.get(), ModBlocks.BIOME_SCANNER_ELITE.get(), ModBlocks.BIOME_SCANNER_ULTIMATE.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityScannerQueue>> SCANNER_QUEUE_TILE = TILE_ENTITIES.register("scanner_queue", () -> TileEntityType.Builder.of(TileEntityScannerQueue::new, ModBlocks.SCANNER_QUEUE.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityTerrainScanner>> TERRAIN_SCANNER_TILE = TILE_ENTITIES.register("terrain_scanner", () -> TileEntityType.Builder.of(TileEntityTerrainScanner::new, ModBlocks.TERRAIN_SCANNER.get()).build(null));
}
