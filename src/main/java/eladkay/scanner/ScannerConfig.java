package eladkay.scanner;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Saad on 9/7/2016.
 *
 * Re-created by Picono435 on 26/01/2024
 */
public class ScannerConfig {

    private static final Pair<ScannerConfig, ForgeConfigSpec> PAIR = new ForgeConfigSpec.Builder()
            .configure(ScannerConfig::new);
    public static final ScannerConfig CONFIG = PAIR.getLeft();
    public static final ForgeConfigSpec SPEC = PAIR.getRight();


    public final ForgeConfigSpec.ConfigValue<Integer> energyPerBlockTerrainScanner;
    public final ForgeConfigSpec.ConfigValue<Integer> maxEnergyBufferTerrain;
    public final ForgeConfigSpec.ConfigValue<Boolean> showOutline;

    public final ForgeConfigSpec.ConfigValue<Integer> minEnergyPerChunkBiomeScanner;
    public final ForgeConfigSpec.ConfigValue<Integer> maxEnergyBufferBiome;
    public final ForgeConfigSpec.ConfigValue<Integer> increase;
    public final ForgeConfigSpec.ConfigValue<Boolean> genExtraVanillaOres;
    public final ForgeConfigSpec.ConfigValue<Boolean> voidOriginalBlock;
    public final ForgeConfigSpec.ConfigValue<Integer> maxSpeedup;
    public final ForgeConfigSpec.ConfigValue<Integer> maxY;
    public final ForgeConfigSpec.ConfigValue<Integer> remoteBuildCost;
    public final ForgeConfigSpec.ConfigValue<Boolean> replaceNonSourceLiquid;
    public final ForgeConfigSpec.ConfigValue<Integer> maxQueueRange;
    public final ForgeConfigSpec.ConfigValue<Integer> queueCapacity;
    public final ForgeConfigSpec.ConfigValue<List<String>> dimensionBlacklist;

    public ScannerConfig(ForgeConfigSpec.Builder builder) {
        this.energyPerBlockTerrainScanner = builder.comment("The amount of energy required for the Terrain Scanner to spawn 1 block").define("energyPerBlockTerrainScanner", 100);
        this.maxEnergyBufferTerrain = builder.comment("The energy buffer of the Terrain Scanner").define("maxEnergyBufferTerrain", 300000);
        //dimid = config.get("Scanner", "dimid", 99, "The ID for the fake overworld dimension").getInt();
        this.showOutline = builder.comment("Should the Terrain Scanner show its area of effect outline with shiny particles").define("showOutline", true);
        this.genExtraVanillaOres = builder.comment("Should the terrain scanner automatically generate extra vanilla ores? MineTweakered ores will always be spawned.").define("genVanillaOres", false);
        this.voidOriginalBlock = builder.comment("Should the terrain scanner void the original block after it copies the block?").define("voidOriginalBlock", false);
        this.replaceNonSourceLiquid = builder.comment("Should the terrain scanner replace existing non-source liquid blocks?").define("replaceNonSourceLiquid", false);

        this.minEnergyPerChunkBiomeScanner = builder.comment("The base amount of energy required for the Biome Scanner to scan one chunk.").define("energyPerChunk", 10000);
        this.maxEnergyBufferBiome = builder.comment("The energy buffer of the Biome Scanner").define("maxEnergyBufferBiome", 1000000);
        this.increase = builder.comment("The biome scanner will take (base * this * distanceInChunks * speedupInBlocksPerTick) RF/Tesla per tick to run.").define("increase", 8);
        this.maxSpeedup = builder.comment("The maximum amount of blocks per tick that the terrain scanner can scan. Power usage is multiplied by the amount of speedup.").define("maxSpeedup", 8);
        this.maxY = builder.comment("The maximum height a terrain scanner will build, starting in y=0.").define("maxY", 150);
        this.remoteBuildCost = builder.comment("How much building remotely (Biome+Terrain) will cost in addition to the regular cost").define("remoteBuildCost", 0);
        this.maxQueueRange = builder.comment("The maximum range for remote building with the scanner queue (default value 0 means no maximum).").define("maxQueueRange", 0);
        this.queueCapacity = builder.comment("The maximum capacity of positions that the scanner queue can handle.").define("queueCapacity", 5);
        this.dimensionBlacklist = builder.comment("The dimensions to not create a fake dimension for. This means the scanner blocks won't work in these dimensions.").define("dimensionBlacklist", Collections.emptyList());
    }
}