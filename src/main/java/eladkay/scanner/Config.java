package eladkay.scanner;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by Saad on 9/7/2016.
 */
public class Config {

    public static int energyPerBlockTerrainScanner;
    public static int maxEnergyBufferTerrain;
    public static int dimid;
    public static boolean showOutline;

    public static int minEnergyPerChunkBiomeScanner;
    public static int maxEnergyBufferBiome;
    public static int increase;
    public static boolean genVanillaOres;
    public static int maxSpeedup;


    public static void initConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        config.load();
        energyPerBlockTerrainScanner = config.get("Scanner", "energyPerBlockTerrainScanner", 100, "The amount of energy required for the Terrain Scanner to spawn 1 block").getInt();
        maxEnergyBufferTerrain = config.get("Scanner", "maxEnergyBufferTerrain", 300000, "The energy buffer of the Terrain Scanner").getInt();
        dimid = config.get("Scanner", "dimid", 99, "The ID for the fake overworld dimension").getInt();
        showOutline = config.get("Scanner", "showOutline", true, "Should the Terrain Scanner show its area of effect outline with shiny particles").getBoolean();
        genVanillaOres = config.get("Scanner", "genVanillaOres", true, "Should the terrain scanner automatically generate vanilla ores? MineTweakered ores will always be spawned.").getBoolean();

        minEnergyPerChunkBiomeScanner = config.get("Scanner", "energyPerChunk", 10000, "The base amount of energy required for the Biome Scanner to scan one chunk.").getInt();
        maxEnergyBufferBiome = config.get("Scanner", "maxEnergyBufferBiome", 1000000, "The energy buffer of the Biome Scanner").getInt();
        increase = config.get("Scanner", "increase", 8, "The biome scanner will take (base * this * distanceInChunks * speedupInBlocksPerTick) RF/Tesla per tick to run.").getInt();
        maxSpeedup = config.get("Scanner", "maxSpeedup", 8, "The maximum amount of blocks per tick that the terrain scanner can scan. Power usage is multiplied by the amount of speedup.").getInt();
        config.save();
    }

}