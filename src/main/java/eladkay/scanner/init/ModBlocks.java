package eladkay.scanner.init;

import eladkay.scanner.biome.BlockBiomeScanner;
import eladkay.scanner.terrain.BlockAirey;
import eladkay.scanner.terrain.BlockScannerQueue;
import eladkay.scanner.terrain.BlockTerrainScanner;
import net.minecraft.block.Block;

public class ModBlocks {
    public static BlockTerrainScanner terrainScanner = new BlockTerrainScanner();
    public static BlockScannerQueue scannerQueue = new BlockScannerQueue();
    public static BlockBiomeScanner biomeScannerBasic = new BlockBiomeScanner(0);
    public static BlockBiomeScanner biomeScannerAdv = new BlockBiomeScanner(1);
    public static BlockBiomeScanner biomeScannerElite = new BlockBiomeScanner(2);
    public static BlockBiomeScanner biomeScannerUltimate = new BlockBiomeScanner(3);
    public static BlockAirey air = new BlockAirey();

    public static final Block[] BLOCKS = {
            biomeScannerBasic,
            biomeScannerAdv,
            biomeScannerElite,
            biomeScannerUltimate,
            air,
            scannerQueue,
            terrainScanner
    };

}
